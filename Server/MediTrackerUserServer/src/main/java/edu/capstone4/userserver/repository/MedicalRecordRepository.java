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

    // 支持分页的自定义查询
    Page<MedicalRecord> findByIsDeletedFalse(Pageable pageable);
    /**
     * 查询所有未被逻辑删除的医疗记录
     *
     * @return 未被删除的医疗记录列表
     */
    List<MedicalRecord> findByIsDeletedFalse();

    /**
     * 根据患者 ID 查询该患者的所有未被逻辑删除的医疗记录
     *
     * @param patientId 患者的唯一标识 ID
     * @return 患者的医疗记录列表
     */
    List<MedicalRecord> findByPatientIdAndIsDeletedFalse(Long patientId);
    List<MedicalRecord> findByPatientAndIsDeletedFalse(User patient);
    /**
     * 根据医生 ID 查询该医生关联的所有未被逻辑删除的医疗记录
     *
     * @param doctorId 医生的唯一标识 ID
     * @return 医生的医疗记录列表
     */
    List<MedicalRecord> findByDoctorIdAndIsDeletedFalse(Long doctorId);

    /**
     * 查询指定诊断日期范围内的所有未被逻辑删除的医疗记录
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 指定日期范围内的医疗记录列表
     */
    List<MedicalRecord> findByDateOfDiagnosisBetweenAndIsDeletedFalse(Date startDate, Date endDate);


    /**
     * 根据医生备注中的关键词查询未被逻辑删除的医疗记录
     *
     * @param keyword 关键词，用于模糊匹配医生备注内容
     * @return 含有指定关键词的医疗记录列表
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.doctorsNotes LIKE %:keyword% AND mr.isDeleted = false")
    List<MedicalRecord> findByKeywordInDoctorsNotes(String keyword);
}
