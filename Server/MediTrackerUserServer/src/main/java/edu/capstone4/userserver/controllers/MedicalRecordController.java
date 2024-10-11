package edu.capstone4.userserver.controllers;

import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.services.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    // 初始化账本
    @PostMapping("/initialize")
    public ResponseEntity<Void> initializeLedger() {
        medicalRecordService.initializeLedger();
        return ResponseEntity.ok().build();
    }

    // 创建新的医疗记录
    @PostMapping
    public ResponseEntity<MedicalRecord> createRecord(@RequestBody MedicalRecord record) {
        MedicalRecord newRecord = medicalRecordService.createRecord(record);
        return ResponseEntity.ok(newRecord);
    }

    // 查询所有未被删除的医疗记录
    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllRecords());
    }

    // 查询单个医疗记录
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getRecordById(@PathVariable Long id) {
        return medicalRecordService.getRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 更新医疗记录
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> updateRecord(@PathVariable Long id, @RequestBody MedicalRecord record) {
        MedicalRecord updatedRecord = medicalRecordService.updateRecord(id, record);
        return updatedRecord != null ? ResponseEntity.ok(updatedRecord) : ResponseEntity.notFound().build();
    }

    // 逻辑删除医疗记录
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
