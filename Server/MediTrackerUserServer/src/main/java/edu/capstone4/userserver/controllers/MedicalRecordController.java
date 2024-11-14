package edu.capstone4.userserver.controllers;

import edu.capstone4.userserver.exceptions.BusinessException;
import edu.capstone4.userserver.exceptions.ErrorCode;
import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.payload.request.MedicalRecordRequest;
import edu.capstone4.userserver.payload.request.MedicalRecordUpdateRequest;
import edu.capstone4.userserver.payload.response.BaseResponse;
import edu.capstone4.userserver.services.DoctorService;
import edu.capstone4.userserver.services.MedicalRecordService;
import edu.capstone4.userserver.services.SharePermissionService;
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
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    @Autowired
    public MedicalRecordService medicalRecordService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private SharePermissionService sharePermissionService;

    // 创建新医疗记录，仅限医生
    @PostMapping("/create")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createRecord(@Valid @RequestBody MedicalRecordRequest request) {
        try {
            MedicalRecord createdRecord = medicalRecordService.createRecord(request);
            return ResponseEntity.ok(new BaseResponse<>(createdRecord));
        } catch (BusinessException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(e.getMessage(), e.getErrorCode().getCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BaseResponse<>(ErrorCode.FAILED_TO_CREATE_MEDICAL_RECORD.getMessage(),
                            ErrorCode.FAILED_TO_CREATE_MEDICAL_RECORD.getCode()));
        }
    }
    // 获取所有未删除的医疗记录，仅限医生
    @GetMapping("/all")
    @PreAuthorize("hasRole('DOCTOR')")

    public ResponseEntity<?> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateOfDiagnosis") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size, sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        Page<MedicalRecord> records = medicalRecordService.getAllRecords(pageable);
        return ResponseEntity.ok(new BaseResponse<>(records));
    }

    // 根据ID获取单个医疗记录，医生可以查看所有记录，患者只能查看自己的记录
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<?> getRecordById(@PathVariable Long id) {
        try {
            MedicalRecord record = medicalRecordService.getRecordById(id);
            return ResponseEntity.ok(record);
        } catch (BusinessException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(e.getMessage(), e.getErrorCode().getCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BaseResponse<>(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                            ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
        }
    }

    // 更新医疗记录，仅限医生
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updateRecord(@PathVariable Long id, @RequestBody MedicalRecordUpdateRequest updatedRecord, @RequestParam Long doctorId) {
        MedicalRecord record = medicalRecordService.updateRecord(id, updatedRecord, doctorId);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }

    // 逻辑删除医疗记录，仅限医生
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> deleteRecord(@PathVariable Long id) {
        try {
            medicalRecordService.deleteRecord(id);
        } catch (BusinessException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(e.getMessage(), e.getErrorCode().getCode()));
        }
        return ResponseEntity.ok(new BaseResponse<>("Medical record deleted successfully."));
    }

    // 上传文件附件
    @PostMapping("/{recordId}/upload")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> uploadFile(@PathVariable Long recordId, @RequestParam("file") MultipartFile file) {
        try {
            Attachment attachment = medicalRecordService.uploadFileToIpfs(recordId, file);
            return new ResponseEntity<>(new BaseResponse<>(attachment), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new BaseResponse<>("File upload failed: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR.getCode()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 下载文件附件
    @GetMapping("/{recordId}/download/{fileId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        try {
            byte[] fileData = medicalRecordService.downloadFileFromIpfs(fileId);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileId);
            return ResponseEntity.ok().headers(headers).body(fileData);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to share a medical record with another doctor.
     *
     * @param recordId   ID of the medical record to share.
     * @param doctorId   ID of the doctor with whom the record is to be shared.
     * @return ResponseEntity with status of sharing operation.
     */
    @PostMapping("/{recordId}/share")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> shareMedicalRecord(@PathVariable Long recordId, @RequestParam Long doctorId) {
        boolean isShared = sharePermissionService.shareRecordWithDoctor(recordId, doctorId);

        if (isShared) {
            return ResponseEntity.ok(new BaseResponse<>("Medical record shared successfully."));
        } else {
            return ResponseEntity.badRequest().body("Unable to share medical record. Please check IDs.");
        }
    }

    /**
     * Endpoint to list all doctors with whom a record is shared.
     *
     * @param recordId ID of the medical record.
     * @return List of doctors with whom the record is shared.
     */
    @GetMapping("/{recordId}/shared-doctors")
    public ResponseEntity<?> getDoctorsWithAccess(@PathVariable Long recordId) {
        List<Doctor> sharedDoctors = sharePermissionService.getDoctorsWithAccessToRecord(recordId);
        return ResponseEntity.ok(new BaseResponse<>(sharedDoctors));
    }

    /**
     * Endpoint to revoke access to a shared medical record from a specific doctor.
     *
     * @param recordId ID of the medical record.
     * @param doctorId ID of the doctor whose access is to be revoked.
     * @return ResponseEntity with status of revocation operation.
     */
    @DeleteMapping("/{recordId}/revoke-access")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> revokeAccess(@PathVariable Long recordId, @RequestParam Long doctorId) {
        boolean isRevoked = sharePermissionService.revokeAccess(recordId, doctorId);

        if (isRevoked) {
            return ResponseEntity.ok(new BaseResponse<>("Access revoked successfully."));
        } else {
            return ResponseEntity.badRequest().body(new BaseResponse<>(ErrorCode.RECORD_ACCESS_REVOCATION_FAILED.getMessage(),
                    ErrorCode.RECORD_ACCESS_REVOCATION_FAILED.getCode()));
        }
    }
}
