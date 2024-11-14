package edu.capstone4.userserver.repository;

import edu.capstone4.userserver.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

     Optional<Doctor> findById(Long aLong);

     boolean existsById(Long aLong);

     boolean existsByPersonalId(String personalId);

     boolean existsByLicense(String license);

     boolean existsByUserId(Long userId);

     boolean existsByProfessionalId(String professionalId);

     Optional<Doctor> findByUserId(Long userId);
}
