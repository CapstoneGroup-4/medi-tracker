package edu.capstone4.userserver.services;

import edu.capstone4.userserver.exception.BusinessException;
import edu.capstone4.userserver.exception.ErrorCode;
import edu.capstone4.userserver.exception.RoleNotFoundException;
import edu.capstone4.userserver.models.ERole;
import edu.capstone4.userserver.models.Role;
import edu.capstone4.userserver.models.User;
import edu.capstone4.userserver.repository.RoleRepository;
import edu.capstone4.userserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


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
            role = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RoleNotFoundException(ErrorCode.ROLE_NOT_FOUND));
        } else {
            switch (strRoles) {
                case "ROLE_ADMIN":
                    role = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RoleNotFoundException(ErrorCode.ROLE_NOT_FOUND));
                    break;
                case "ROLE_DOCTOR":
                    role = roleRepository.findByName(ERole.ROLE_DOCTOR)
                            .orElseThrow(() -> new RoleNotFoundException(ErrorCode.ROLE_NOT_FOUND));
                    break;
                default:
                    role = roleRepository.findByName(ERole.ROLE_USER)
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
