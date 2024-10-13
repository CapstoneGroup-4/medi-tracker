package edu.capstone4.userserver.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import edu.capstone4.userserver.config.ErrorCodeConfig;
import edu.capstone4.userserver.properties.ErrorCodeProperties;
import edu.capstone4.userserver.events.registers.RegistrationCompleteEvent;
import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.models.ERole;
import edu.capstone4.userserver.models.Role;
import edu.capstone4.userserver.models.User;
import edu.capstone4.userserver.payload.request.DoctorSignupRequest;
import edu.capstone4.userserver.payload.request.LoginRequest;
import edu.capstone4.userserver.payload.request.SignupRequest;
import edu.capstone4.userserver.payload.response.BaseResponse;
import edu.capstone4.userserver.payload.response.JwtResponse;
import edu.capstone4.userserver.payload.response.SignupResponse;
import edu.capstone4.userserver.repository.DoctorRepository;
import edu.capstone4.userserver.repository.RoleRepository;
import edu.capstone4.userserver.repository.UserRepository;
import edu.capstone4.userserver.jwt.JwtUtils;
import edu.capstone4.userserver.services.UserDetailsImpl;
import edu.capstone4.userserver.services.UserDetailsServiceImpl;
import edu.capstone4.userserver.services.VerificationCodeService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private ErrorCodeProperties errorCodeProperties;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping("/isactive/{email}")
    public ResponseEntity<?> getUserActiveStatus(@PathVariable("email") String email) {
        if (userRepository.existsByEmailAndEnabled(email, true)) {
            return ResponseEntity.ok(new BaseResponse<>("User is active."));
        }

        // 从配置中获取错误码和消息
        ErrorCodeConfig errorCodeConfig = errorCodeProperties.getCode("user-not-active");
        return ResponseEntity.ok(new BaseResponse<>(errorCodeConfig.getMessage(), errorCodeConfig.getCode()));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new BaseResponse<>(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(errorCodeProperties.getCode("user-name-exist").getMessage(),
                            errorCodeProperties.getCode("user-name-exist").getCode()));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(errorCodeProperties.getCode("user-email-exist").getMessage(),
                            errorCodeProperties.getCode("user-email-exist").getCode()));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), signUpRequest.getGender());

        String strRoles = signUpRequest.getRole();
        Role role;

        if (strRoles == null) {
            role = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            switch (strRoles) {
                case "admin":
                    role = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    break;
                case "doc":
                    role = roleRepository.findByName(ERole.ROLE_DOCTOR)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    break;
                default:
                    role = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            }
        }

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        Integer age = signUpRequest.getAge();
        if (age != null) {
            user.setAge(age);
        }

        String sin = signUpRequest.getSin();
        if (sin != null) {
            user.setSin(sin);
        }

        String phone = signUpRequest.getPhone();
        if (phone != null) {
            user.setPhone(phone);
        }

        // verification code not verified
        user.setEnabled(false);

        userRepository.save(user);

        if (ERole.ROLE_USER.equals(role.getName())) {
            // 生成验证码
            String verificationCode = verificationCodeService.generateCode();
            // 发布用户注册完成事件
            eventPublisher.publishEvent(new RegistrationCompleteEvent(user.getEmail(), verificationCode));
            return ResponseEntity.ok(new BaseResponse<>("User registered successfully! A verification code has been sent to your email."));
        }

        // 其他用户角色需要进一步验证
        User userDetails = userRepository.findByUsername(signUpRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + signUpRequest.getUsername()));

        return ResponseEntity.ok(new BaseResponse<>(new SignupResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail())));
    }

    @PostMapping("/doctor-signup")
    public ResponseEntity<?> doctorVerification(@Valid @RequestBody DoctorSignupRequest doctorSignupRequest) {
        // 检查医生是否已存在
        if (doctorRepository.existsByPersonalId(doctorSignupRequest.getPersonalId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(errorCodeProperties.getCode("doctor-exist").getMessage(),
                            errorCodeProperties.getCode("doctor-exist").getCode()));
        }

        // 获取User对象，假设请求包含用户ID，用来与Doctor关联
        Optional<User> userOptional = userRepository.findById(doctorSignupRequest.getUserId());
        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(errorCodeProperties.getCode("user-not-found").getMessage(),
                            errorCodeProperties.getCode("user-not-found").getCode()));
        }

        User user = userOptional.get();

        // 创建并保存 Doctor 实体
        Doctor doctor = new Doctor(
                doctorSignupRequest.getProfessionalId(),
                doctorSignupRequest.getPersonalId(),
                doctorSignupRequest.getLicense(),
                doctorSignupRequest.getLicenseAuthority(),
                doctorSignupRequest.getJobTitle(),
                doctorSignupRequest.getSpecialization(),
                doctorSignupRequest.getClinicName()
        );

        // 关联 User 对象
        doctor.setUser(user);
        doctorRepository.save(doctor);

        // 获取关联的 User email
        String email = user.getEmail();

        // 生成验证码
        String verificationCode = verificationCodeService.generateCode();

        // 发布用户注册完成事件
        eventPublisher.publishEvent(new RegistrationCompleteEvent(email, verificationCode));

        return ResponseEntity.ok(new BaseResponse<>("Doctor registered successfully! A verification code has been sent to your email."));
    }

}
