package edu.capstone4.userserver.repository;

import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.models.SharePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharePermissionRepository extends JpaRepository<SharePermission, Long> {

    boolean existsByMedicalRecordIdAndDoctorId(Long medicalRecordId, Long doctorId);

    boolean existsByMedicalRecordAndDoctor(MedicalRecord record, Doctor doctor);

    Optional<SharePermission> findByMedicalRecordAndDoctor(MedicalRecord record, Doctor doctor);

    @Query("SELECT sp.doctor FROM SharePermission sp WHERE sp.medicalRecord = :record")
    List<Doctor> findDoctorsByMedicalRecord(MedicalRecord record);
}
