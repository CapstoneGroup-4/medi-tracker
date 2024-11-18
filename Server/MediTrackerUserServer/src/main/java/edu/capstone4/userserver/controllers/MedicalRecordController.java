package edu.capstone4.userserver.controllers;

import edu.capstone4.userserver.exceptions.BusinessException;
import edu.capstone4.userserver.exceptions.ErrorCode;
import edu.capstone4.userserver.jwt.AuthTokenFilter;
import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.models.User;
import edu.capstone4.userserver.payload.request.MedicalRecordRequest;
import edu.capstone4.userserver.payload.request.MedicalRecordUpdateRequest;
import edu.capstone4.userserver.payload.response.AttachmentResponse;
import edu.capstone4.userserver.payload.response.BaseResponse;
import edu.capstone4.userserver.payload.response.MedicalRecordResponse;
import edu.capstone4.userserver.services.DoctorService;
import edu.capstone4.userserver.services.MedicalRecordService;
import edu.capstone4.userserver.services.SharePermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private SharePermissionService sharePermissionService;

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);

    // 创建新医疗记录，仅限医生
    @PostMapping("/create")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createRecord(@Valid @RequestBody MedicalRecordRequest request) {
        try {
            MedicalRecord createdRecord = medicalRecordService.createRecord(request);

            MedicalRecordResponse recordResponse = getMedicalRecordResponse(createdRecord);

            return ResponseEntity.ok(new BaseResponse<>(recordResponse));
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

    private MedicalRecordResponse getMedicalRecordResponse(MedicalRecord createdRecord) {
        MedicalRecordResponse recordResponse = new MedicalRecordResponse();
        recordResponse.setId(createdRecord.getId());
        recordResponse.setRecordId(createdRecord.getRecordSerialNumber());
        recordResponse.setDateOfDiagnosis(createdRecord.getDateOfDiagnosis());
        recordResponse.setComment(createdRecord.getComment());
        recordResponse.setPrimaryDiagnosis(createdRecord.getPrimaryDiagnosis());
        // Access lazy fields to ensure they are initialized
        Doctor creatorDoctor = createdRecord.getCreatorDoctor();
        User user = createdRecord.getUser();

        recordResponse.setClinicName(creatorDoctor.getClinicName());
        recordResponse.setDoctorName(user.getUsername());
        recordResponse.setPatientName(user.getUsername());

        recordResponse.setAge(user.getAge());
        recordResponse.setGender(user.getGender());
        recordResponse.setSin(user.getSin());

        return recordResponse;
    }

    // 获取所有未删除的医疗记录，仅限医生
    @GetMapping("/all")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    public ResponseEntity<?> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateOfDiagnosis") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size, sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        try {
            Page<MedicalRecord> records = medicalRecordService.getAllRecords(pageable);
            Page<MedicalRecordResponse> recordResponses = records.map(this::getMedicalRecordResponse);
            return ResponseEntity.ok(new BaseResponse<>(recordResponses));
        } catch (BusinessException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(e.getMessage(), e.getErrorCode().getCode()));
        }
    }

    // 根据ID获取单个医疗记录，医生可以查看所有记录，患者只能查看自己的记录
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    public ResponseEntity<?> getRecordById(@PathVariable Long id) {
        try {
            MedicalRecord record = medicalRecordService.getRecordById(id);
            MedicalRecordResponse recordResponse = getMedicalRecordResponse(record);
            return ResponseEntity.ok(new BaseResponse<>(recordResponse));
        } catch (BusinessException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(e.getMessage(), e.getErrorCode().getCode()));
        } catch (Exception e) {
            logger.error("Error occurred while fetching medical record: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BaseResponse<>(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                            ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
        }
    }

    // 更新医疗记录，仅限医生
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updateRecord(@PathVariable Long id, @RequestBody MedicalRecordUpdateRequest updatedRecord) {
        MedicalRecord record = medicalRecordService.updateRecord(id, updatedRecord);
        if (record == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(ErrorCode.DOCTOR_NOT_FOUND.getMessage(), ErrorCode.DOCTOR_NOT_FOUND.getCode()));
        }
        MedicalRecordResponse recordResponse = getMedicalRecordResponse(record);
        return ResponseEntity.ok(new BaseResponse<>(recordResponse));
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

            AttachmentResponse responseAttachment = getAttachmentResponse(attachment);
            return new ResponseEntity<>(new BaseResponse<>(responseAttachment), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new BaseResponse<>("File upload failed: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR.getCode()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BusinessException e) {
            return new ResponseEntity<>(new BaseResponse<>(e.getMessage(), e.getErrorCode().getCode()), HttpStatus.BAD_REQUEST);
        }
    }

    private AttachmentResponse getAttachmentResponse(Attachment attachment) {
        AttachmentResponse responseAttachment = new AttachmentResponse();
        responseAttachment.setAttachmentId(attachment.getId());
        responseAttachment.setAttachmentName(attachment.getFileName());
        responseAttachment.setIpfsHash(attachment.getIpfsHash());
        responseAttachment.setUploadDate(attachment.getUploadDate());
        responseAttachment.setRecordId(attachment.getMedicalRecord().getId());
        responseAttachment.setRecordNo(attachment.getMedicalRecord().getRecordSerialNumber());
        return responseAttachment;
    }

    // 下载文件附件
    @GetMapping("/{recordId}/download/{fileId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long recordId, @PathVariable Long fileId) {
        try {
            byte[] fileData = medicalRecordService.downloadFileFromIpfs(recordId, fileId);
            HttpHeaders headers = new HttpHeaders();
            String fileName = medicalRecordService.getAttachmentNameById(fileId);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            return ResponseEntity.ok().headers(headers).body(fileData);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to get all attachments for a specific medical record.
     *
     * @param recordId ID of the medical record.
     * @return List of attachments associated with the medical record.
     */
    @GetMapping("/{recordId}/attachments")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('USER')")
    public ResponseEntity<?> getAttachmentsByRecordId(@PathVariable Long recordId) {
        try {
            List<Attachment> attachments = medicalRecordService.getAttachmentsByRecordId(recordId);

            // Convert to DTO for response
            List<AttachmentResponse> attachmentResponses = attachments.stream()
                    .map(this::getAttachmentResponse)
                    .toList();

            return ResponseEntity.ok(new BaseResponse<>(attachmentResponses));
        } catch (BusinessException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(e.getMessage(), e.getErrorCode().getCode()));
        } catch (Exception e) {
            logger.error("Error occurred while fetching attachments for record ID {}: {}", recordId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new BaseResponse<>(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                            ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
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
    @PreAuthorize("hasRole('DOCTOR')")
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
