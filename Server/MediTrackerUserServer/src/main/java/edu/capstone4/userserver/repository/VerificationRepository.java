package edu.capstone4.userserver.repository;

import edu.capstone4.userserver.models.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
    Optional<Verification> findByEmail(String email);
}
