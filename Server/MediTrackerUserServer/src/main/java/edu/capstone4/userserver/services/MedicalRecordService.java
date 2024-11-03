package edu.capstone4.userserver.services;

import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import edu.capstone4.userserver.models.Attachment;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.capstone4.userserver.repository.AttachmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    private FileService fileService;

    @Autowired
    private AttachmentRepository attachmentRepository;

    // 初始化账本，添加样例医疗记录
    public void initializeLedger() {
        MedicalRecord record1 = new MedicalRecord();
        record1.setPatientName("John Doe");
        record1.setGender("Male");
        record1.setDateOfBirth(new Date());
        record1.setRecordNo("PA123456");
        record1.setPrimaryDiagnosis("Diabetes");
        record1.setDateOfDiagnosis(new Date());
        record1.setDoctorsNotes("Monitor blood sugar levels daily.");
        record1.setTreatmentPlan("Diet modification and medication.");
        record1.setNextSteps("Regular checkups.");
        record1.setTreatmentStatus("Ongoing");
        record1.setPhysicianName("Dr. Smith");
        record1.setMedicationName("Metformin");
        record1.setDosage("500 mg");
        record1.setFrequency("Twice daily");
        record1.setDuration("6 months");
        record1.setInstructions("Take with food.");
        record1.setPrescribingDoctor("Dr. Smith");
        medicalRecordRepository.save(record1);
    }

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordService.class);



    // 创建新的医疗记录
    public MedicalRecord createRecord(MedicalRecord record) {
        logger.info("Creating new medical record for patient: {}", record.getPatientName());
        return medicalRecordRepository.save(record);
    }


    // 查询所有未删除的医疗记录
    public Page<MedicalRecord> getAllRecords(Pageable pageable) {
        logger.info("Fetching all non-deleted medical records with pagination.");
        return medicalRecordRepository.findByIsDeletedFalse(pageable);
    }

    // 根据ID查询单个医疗记录
    public Optional<MedicalRecord> getRecordById(Long id) {
        return medicalRecordRepository.findById(id);
    }

    // 更新医疗记录
    public MedicalRecord updateRecord(Long id, MedicalRecord updatedRecord) {
        return medicalRecordRepository.findById(id)
                .map(record -> {
                    record.setPatientName(updatedRecord.getPatientName());
                    record.setGender(updatedRecord.getGender());
                    record.setDateOfBirth(updatedRecord.getDateOfBirth());
                    record.setRecordNo(updatedRecord.getRecordNo());
                    record.setSin(updatedRecord.getSin());
                    record.setNik(updatedRecord.getNik());
                    record.setPrimaryDiagnosis(updatedRecord.getPrimaryDiagnosis());
                    record.setDateOfDiagnosis(updatedRecord.getDateOfDiagnosis());
                    record.setDoctorsNotes(updatedRecord.getDoctorsNotes());
                    record.setTreatmentPlan(updatedRecord.getTreatmentPlan());
                    record.setNextSteps(updatedRecord.getNextSteps());
                    record.setTreatmentStatus(updatedRecord.getTreatmentStatus());
                    record.setPhysicianName(updatedRecord.getPhysicianName());
                    record.setMedicationName(updatedRecord.getMedicationName());
                    record.setDosage(updatedRecord.getDosage());
                    record.setFrequency(updatedRecord.getFrequency());
                    record.setDuration(updatedRecord.getDuration());
                    record.setInstructions(updatedRecord.getInstructions());
                    record.setPrescribingDoctor(updatedRecord.getPrescribingDoctor());
                    return medicalRecordRepository.save(record);
                }).orElse(null);
    }

    // 逻辑删除医疗记录
    public void deleteRecord(Long id) {
        logger.info("Deleting medical record with ID: {}", id);
        medicalRecordRepository.findById(id).ifPresent(record -> {
            record.setDeleted(true);
            medicalRecordRepository.save(record);
        });
    }

    // 上传文件并关联到指定的医疗记录
    public Attachment uploadFile(Long recordId, MultipartFile file) throws IOException {
        Optional<MedicalRecord> recordOptional = medicalRecordRepository.findById(recordId);
        if (recordOptional.isPresent()) {
            MedicalRecord record = recordOptional.get();

            // 假设 Attachment 包含 fileName、fileType、fileData 字段
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

    // 下载文件
    public byte[] downloadFile(Long recordId, Long fileId) throws IOException {
        Optional<Attachment> attachmentOptional = attachmentRepository.findById(fileId);
        if (attachmentOptional.isPresent()) {
            return attachmentOptional.get().getFileData();
        } else {
            throw new IOException("File not found");
        }
    }

}
