package edu.capstone4.userserver.repository;

import edu.capstone4.userserver.models.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    // 根据医疗记录 ID 获取附件列表
    List<Attachment> findByMedicalRecordId(Long medicalRecordId);

    // 根据文件类型查询附件
    List<Attachment> findByFileType(String fileType);

    // 根据上传日期范围查询附件
    List<Attachment> findByUploadDateBetween(Date startDate, Date endDate);

    // 根据文件名关键字查询附件
    List<Attachment> findByFileNameContaining(String keyword);

    // 获取指定医疗记录 ID 和文件 ID 的附件
    Optional<Attachment> findByMedicalRecordIdAndFileId(Long medicalRecordId, Long fileId);
}