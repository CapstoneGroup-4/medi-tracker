package edu.capstone4.userserver.repository;

import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    @EntityGraph(attributePaths = {"creatorDoctor.user", "user", "attachments"})
    Optional<MedicalRecord> findById(Long id);

    // Fetch records created by a doctor or shared with the doctor
    @EntityGraph(attributePaths = {"creatorDoctor.user", "sharedWithDoctors.doctor.user", "user"})
    Page<MedicalRecord> findAllByCreatorDoctor_IdOrSharedWithDoctors_Doctor_Id(Long creatorDoctorId, Long sharedDoctorId, Pageable pageable);

    // Fetch records associated with a specific user
    @EntityGraph(attributePaths = {"user", "creatorDoctor.user"})
    Page<MedicalRecord> findAllByUser_Id(Long userId, Pageable pageable);
}
