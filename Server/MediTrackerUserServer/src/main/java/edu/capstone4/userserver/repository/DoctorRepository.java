package edu.capstone4.userserver.repository;

import edu.capstone4.userserver.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
//public interface DoctorRepository extends JpaRepository<Doctor, Long> {
//     Optional<Doctor> findByPersonalId(String personalId);
//     boolean existsByPersonalId(String personalId);
//}
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

     Optional<Doctor> findById(Long aLong);

     boolean existsById(Long aLong);

     boolean existsByPersonalId(String personalId);
}