package edu.capstone4.userserver.repository;

import edu.capstone4.userserver.models.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    // 查询未被逻辑删除的记录
    List<MedicalRecord> findByIsDeletedFalse();
}
