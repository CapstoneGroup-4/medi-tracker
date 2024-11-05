package edu.capstone4.userserver.repository;

import java.util.Optional;

import edu.capstone4.userserver.models.ERole;
import edu.capstone4.userserver.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
