package edu.capstone4.userserver.repository;

import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // 支持分页的自定义查询，仍然可以用于与链码交互同步
    Page<MedicalRecord> findByIsDeletedFalse(Pageable pageable);

    // 其他查询方法保留，以便链码与数据库进行同步，或者用于链码操作的回显数据
    List<MedicalRecord> findByIsDeletedFalse();
    List<MedicalRecord> findByPatientIdAndIsDeletedFalse(Long patientId);
    List<MedicalRecord> findByPatientAndIsDeletedFalse(User patient);
    List<MedicalRecord> findByDoctorIdAndIsDeletedFalse(Long doctorId);
    List<MedicalRecord> findByDateOfDiagnosisBetweenAndIsDeletedFalse(Date startDate, Date endDate);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.doctorsNotes LIKE %:keyword% AND mr.isDeleted = false")
    List<MedicalRecord> findByKeywordInDoctorsNotes(String keyword);
}
