package edu.capstone4.userserver.controllers;

import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.services.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import edu.capstone4.userserver.models.Attachment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // 初始化账本，仅限医生 （测试使用）
    private static final Logger logger = Logger.getLogger(MedicalRecordController.class.getName());
    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> initializeLedger() {
        if (medicalRecordService.isLedgerInitialized()) {
            logger.info("Attempt to initialize ledger when it is already initialized.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ledger already initialized.");
        }
        medicalRecordService.initializeLedger();
        logger.info("Ledger successfully initialized with sample data.");
        return ResponseEntity.ok("Ledger initialized with sample data.");
    }


    // 创建新医疗记录，仅限医生
    @PostMapping("/create")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createRecord(@Valid @RequestBody MedicalRecord record, @RequestParam Long patientId, @RequestParam Long doctorId) {
        try {
            // 确保对嵌套的患者和医生对象进行了处理
            if (record.getPatient() == null || record.getDoctor() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Patient and Doctor information is required.");
            }
            String transactionResult = medicalRecordService.createRecordInChaincode(record, patientId, doctorId);
            return ResponseEntity.ok("Record created successfully: " + transactionResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create record: " + e.getMessage());
        }
    }

    // 获取所有未删除的医疗记录，仅限医生
    @GetMapping("/all")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Page<MedicalRecord>> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateOfDiagnosis") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size, sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        Page<MedicalRecord> records = medicalRecordService.getAllRecordsFromChaincode(pageable);
        return ResponseEntity.ok(records);
    }

    // 根据ID获取单个医疗记录，医生可以查看所有记录，患者只能查看自己的记录
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<MedicalRecord> getRecordById(@PathVariable Long id) {
        Optional<MedicalRecord> record = medicalRecordService.getRecordByIdFromChaincode(id);
        return record.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 更新医疗记录，仅限医生
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> updateRecord(@PathVariable Long id, @RequestBody MedicalRecord updatedRecord) {
        try {
            String transactionResult = medicalRecordService.updateRecordInChaincode(id, updatedRecord);
            return ResponseEntity.ok("Record updated successfully: " + transactionResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update record: " + e.getMessage());
        }
    }

    // 逻辑删除医疗记录，仅限医生
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> deleteRecord(@PathVariable Long id) {
        try {
            String transactionResult = medicalRecordService.deleteRecordInChaincode(id);
            return ResponseEntity.ok("Record deleted successfully: " + transactionResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete record: " + e.getMessage());
        }
    }

    // 上传文件附件
    @PostMapping("/{recordId}/upload")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> uploadFile(@PathVariable Long recordId, @RequestParam("file") MultipartFile file) {
        try {
            Attachment attachment = medicalRecordService.uploadFile(recordId, file);
            return new ResponseEntity<>(attachment, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 下载文件附件
    @GetMapping("/{recordId}/download/{fileId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long recordId, @PathVariable Long fileId) {
        try {
            byte[] fileData = medicalRecordService.downloadFile(recordId, fileId);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileId);
            return ResponseEntity.ok().headers(headers).body(fileData);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
