package edu.capstone4.userserver.services;


import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.models.Attachment;
//import edu.capstone4.userserver.models.User;
//import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.repository.MedicalRecordRepository;
import edu.capstone4.userserver.utils.EncryptionUtil;
import edu.capstone4.userserver.repository.AttachmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class MedicalRecordService {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordService.class);

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    public void preprocessRecord(MedicalRecord record) throws Exception {
        try {
            // 加密 SIN 和 NIK
            SecretKey key = EncryptionUtil.generateKey();
            String encryptedSin = EncryptionUtil.encrypt(record.getSin(), key);
            String encryptedNik = EncryptionUtil.encrypt(record.getNik(), key);

            // 设置加密后的值
            record.setSin(encryptedSin);
            record.setNik(encryptedNik);

            // 去除患者姓名前后空格
            record.setPatientName(record.getPatientName().trim());

            // 解析日期
            String dateStr = String.valueOf(record.getDateOfDiagnosis()); // 假设为字符串
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            record.setDateOfDiagnosis(date);
        } catch (ParseException e) {
            // 记录错误信息
            logger.error("Error while parsing the date for medical record: {}", record.getId(), e);
        }
    }

    // 初始化账本：改为调用链码
    public boolean isLedgerInitialized() {
        // 假设判断标准是检查数据库中是否已有记录，可以使用计数逻辑
        return medicalRecordRepository.count() > 0;
    }

    public void initializeLedger() {
        logger.info("Initializing ledger with sample data");
        // 调用链码初始化账本逻辑
    }

    // 创建新的医疗记录：改为通过链码创建
    public String createRecordInChaincode(MedicalRecord record, Long patientId, Long doctorId) {
        // 检查嵌套对象的有效性
        if (record.getPatient() == null || record.getDoctor() == null) {
            throw new IllegalArgumentException("Patient and Doctor information must be provided.");
        }

        logger.info("Creating new medical record in chaincode for patient ID: {}", patientId);
        // 调用链码以创建医疗记录，返回交易 ID 或状态信息
        return "Transaction ID";
    }

    // 查询所有未删除的医疗记录：通过链码查询
    public Page<MedicalRecord> getAllRecordsFromChaincode(Pageable pageable) {
        logger.info("Fetching all non-deleted medical records from chaincode with pagination.");
        // 调用链码分页查询医疗记录，返回分页结果
        return Page.empty(); // 示例，仅供测试
    }

    // 根据ID查询单个医疗记录：通过链码查询
    public Optional<MedicalRecord> getRecordByIdFromChaincode(Long id) {
        logger.info("Fetching medical record by ID from chaincode: {}", id);
        // 调用链码查询指定 ID 的医疗记录
        return Optional.empty(); // 示例，仅供测试
    }

    // 更新医疗记录：通过链码更新
    public String updateRecordInChaincode(Long id, MedicalRecord updatedRecord) {
        logger.info("Updating medical record in chaincode for ID: {}", id);
        // 调用链码执行更新操作，返回交易结果
        return "Transaction ID";
    }

    // 逻辑删除医疗记录：通过链码进行逻辑删除
    public String deleteRecordInChaincode(Long id) {
        logger.info("Deleting medical record in chaincode with ID: {}", id);
        // 调用链码标记记录为已删除，返回交易结果
        return "Transaction ID";
    }

    // 上传文件并关联到指定的医疗记录（保留原逻辑）
    public Attachment uploadFile(Long recordId, MultipartFile file) throws IOException {
        Optional<MedicalRecord> recordOptional = medicalRecordRepository.findById(recordId);
        if (recordOptional.isPresent()) {
            MedicalRecord record = recordOptional.get();

            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileType(file.getContentType());
            attachment.setFileData(file.getBytes());
            attachment.setMedicalRecord(record);

            record.getAttachments().add(attachment);
            attachmentRepository.save(attachment);

            return attachment;
        } else {
            throw new IOException("MedicalRecord not found");
        }
    }

    // 下载文件（保留原逻辑）
    public byte[] downloadFile(Long recordId, Long fileId) throws IOException {
        Optional<Attachment> attachmentOptional = attachmentRepository.findById(fileId);
        if (attachmentOptional.isPresent()) {
            return attachmentOptional.get().getFileData();
        } else {
            throw new IOException("File not found");
        }
    }
}