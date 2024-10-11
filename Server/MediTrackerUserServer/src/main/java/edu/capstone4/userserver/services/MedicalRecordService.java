package edu.capstone4.userserver.services;

import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    // 初始化账本，添加样例医疗记录
    public void initializeLedger() {
        MedicalRecord record1 = new MedicalRecord();
        record1.setPatientName("John Doe");
        record1.setDiagnosis("Diabetes");
        record1.setDoctor("Dr. Smith");
        medicalRecordRepository.save(record1);

        MedicalRecord record2 = new MedicalRecord();
        record2.setPatientName("Jane Roe");
        record2.setDiagnosis("Hypertension");
        record2.setDoctor("Dr. White");
        medicalRecordRepository.save(record2);
    }

    // 创建新的医疗记录
    public MedicalRecord createRecord(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    // 查询所有未删除的医疗记录
    public List<MedicalRecord> getAllRecords() {
        return medicalRecordRepository.findByIsDeletedFalse();
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
                    record.setDiagnosis(updatedRecord.getDiagnosis());
                    record.setDoctor(updatedRecord.getDoctor());
                    return medicalRecordRepository.save(record);
                }).orElse(null);
    }

    // 逻辑删除医疗记录
    public void deleteRecord(Long id) {
        medicalRecordRepository.findById(id).ifPresent(record -> {
            record.setIsDeleted(true);
            medicalRecordRepository.save(record);
        });
    }
}
