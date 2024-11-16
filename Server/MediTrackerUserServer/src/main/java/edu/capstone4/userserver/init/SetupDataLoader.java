package edu.capstone4.userserver.init;

import edu.capstone4.userserver.models.ERole;
import edu.capstone4.userserver.models.Role;
import edu.capstone4.userserver.repository.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetupDataLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public SetupDataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        createRoleIfNotFound(ERole.USER);
        createRoleIfNotFound(ERole.ADMIN);
        createRoleIfNotFound(ERole.DOCTOR);
    }

    private void createRoleIfNotFound(ERole role) {
        roleRepository.findByName(role).orElseGet(() -> {
            roleRepository.save(new Role(role));
            return null;
        });
    }
}
