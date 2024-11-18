package edu.capstone4.userserver.services;

import edu.capstone4.userserver.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import edu.capstone4.userserver.repository.UserRepository;
import edu.capstone4.userserver.exceptions.ResourceNotFoundException;
import java.util.List;
import edu.capstone4.userserver.exceptions.BusinessException;
import edu.capstone4.userserver.exceptions.ErrorCode;
import edu.capstone4.userserver.exceptions.RoleNotFoundException;
import edu.capstone4.userserver.models.ERole;
import edu.capstone4.userserver.models.Role;
import edu.capstone4.userserver.repository.RoleRepository;

@Service
public class UserService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void checkRegisterUserValidation(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_EMAIL_EXIST);
        }

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.USER_NAME_EXIST);
        }
    }

    public Role assignRoleToUser(String strRoles) {
        Role role;
        if (strRoles == null) {
            role = roleRepository.findByName(ERole.USER)
                    .orElseThrow(() -> new RoleNotFoundException(ErrorCode.ROLE_NOT_FOUND));
        } else {
            switch (strRoles) {
                case "ADMIN":
                    throw new RoleNotFoundException(ErrorCode.ROLE_NOT_FOUND);
                case "DOCTOR":
                    role = roleRepository.findByName(ERole.DOCTOR)
                            .orElseThrow(() -> new RoleNotFoundException(ErrorCode.ROLE_NOT_FOUND));
                    break;
                default:
                    role = roleRepository.findByName(ERole.USER)
                            .orElseThrow(() -> new RoleNotFoundException(ErrorCode.ROLE_NOT_FOUND));
            }
        }

        return role;
    }

    public void activateUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
