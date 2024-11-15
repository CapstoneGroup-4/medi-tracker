package edu.capstone4.userserver.services;

import edu.capstone4.userserver.exceptions.BusinessException;
import edu.capstone4.userserver.exceptions.ErrorCode;
import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.models.Attachment;
import edu.capstone4.userserver.models.User;
import edu.capstone4.userserver.payload.request.MedicalRecordRequest;
import edu.capstone4.userserver.payload.request.MedicalRecordUpdateRequest;
import edu.capstone4.userserver.repository.DoctorRepository;
import edu.capstone4.userserver.repository.MedicalRecordRepository;
import edu.capstone4.userserver.repository.AttachmentRepository;
import edu.capstone4.userserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private SharePermissionService sharePermissionService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private IPFSService ipfsService;

    @Autowired
    private RecordSerialNumberService recordSerialNumberService;

    public MedicalRecord createRecord(MedicalRecordRequest request) {
        if (!userService.existsByEmail(request.getUserEmail())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        User user = userService.getUserByEmail(request.getUserEmail());
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND));

        MedicalRecord record = new MedicalRecord();
        record.setCreatorDoctor(doctor);
        record.setUser(user);
        record.setClinicName(doctor.getClinicName());
        record.setPrimaryDiagnosis(request.getPrimaryDiagnosis());
        record.setRecordVersion(1);
        record.setDateOfDiagnosis(record.getDateOfDiagnosis());
        record.setComment(request.getComment());
        record.setRecordSerialNumber(recordSerialNumberService.generateSerialNumber());

        return medicalRecordRepository.save(record);
    }

    // Fetch paginated medical records based on user or doctor permissions
    public Page<MedicalRecord> getAllRecords(Pageable pageable) {
        logger.info("Fetching all medical records, with pagination, sorted by last update.");
        // Fetch records based on the user's role
        // Get the current authenticated user
        Long userId = AuthUtils.getCurrentUserId();
        boolean isDoctor = AuthUtils.isDoctor();

        logger.info(" Current user ID: {}, isDoctor: {}", userId, isDoctor);

        Optional<Page<MedicalRecord>> optionalRecords;

        if (isDoctor) {
            Long doctorId = doctorRepository.findByUserId(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND))
                    .getId();
            optionalRecords = Optional.ofNullable(medicalRecordRepository
                    .findAllByCreatorDoctor_IdOrSharedWithDoctors_Doctor_Id(doctorId, doctorId, pageable));
        } else {
            optionalRecords = Optional.ofNullable(medicalRecordRepository
                    .findAllByUser_Id(userId, pageable));
        }

        if (optionalRecords.isPresent() && !optionalRecords.get().isEmpty()) {
            return optionalRecords.get();
        } else {
            logger.info("No records found for user ID: {}", userId);
            return Page.empty();
        }
    }

    // Fetch a single medical record by its ID
    public MedicalRecord getRecordById(Long id) {
        logger.info("Fetching medical record with ID: {}", id);
        MedicalRecord record = checkCanAccessRecord(id);

        if (record == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return record;
    }

    // Update an existing medical record by its ID
    public MedicalRecord updateRecord(Long id, MedicalRecordUpdateRequest updatedRecordRequest) {
        logger.info("Updating medical record with ID: {}", id);

        Long userId = AuthUtils.getCurrentUserId();

        Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(id);
        if (recordOpt.isPresent()) {
            MedicalRecord record = recordOpt.get();
            Long doctorId = doctorRepository.findByUserId(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND))
                    .getId();
            // Find the doctor by doctorId
            doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND));

            if (!record.getCreatorDoctor().getId().equals(doctorId)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
            record.setPrimaryDiagnosis(updatedRecordRequest.getPrimaryDiagnosis());
            record.setDateOfDiagnosis(updatedRecordRequest.getDateOfDiagnosis());
            record.setComment(updatedRecordRequest.getComment());
            record.setDateOfLastUpdate(new Date());
            record.setRecordVersion(record.getRecordVersion() + 1); // Increment version
            return medicalRecordRepository.save(record);
        } else {
            throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }
    }

    // Delete (soft delete) a medical record by setting a deleted flag or removing it completely
    public void deleteRecord(Long id) {
        logger.info("Deleting medical record with ID: {}", id);

        Long userId = AuthUtils.getCurrentUserId();

        Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(id);
        if (recordOpt.isPresent()) {
            MedicalRecord record =  recordOpt.get();

            Long doctorId = doctorRepository.findByUserId(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND))
                    .getId();
            if (!record.getCreatorDoctor().getId().equals(doctorId)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            record.setDeleted(true);
            medicalRecordRepository.save(record);
        } else {
            throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }
    }

    @Transactional
    public Attachment uploadFileToIpfs(Long recordId, MultipartFile file) throws IOException, BusinessException {
        Long doctorId = getDoctorIdByUserId();

        Optional<MedicalRecord> recordOptional = medicalRecordRepository.findById(recordId);
        if (recordOptional.isPresent()) {
            MedicalRecord record = recordOptional.get();

            if (!record.getCreatorDoctor().getId().equals(doctorId)) {
                logger.warn("Unauthorized access attempt by doctor: {} for record: {}", doctorId, record.getCreatorDoctor().getId());
                throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            // Upload to IPFS
            String ipfsHash = ipfsService.uploadFile(file);

            // Create Attachment with IPFS hash
            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileType(file.getContentType());
            attachment.setIpfsHash(ipfsHash);
            attachment.setUploadDate(new Date());
            attachment.setMedicalRecord(record);

            if (record.getAttachments() == null) {
                record.setAttachments(new ArrayList<>());
            }

            record.getAttachments().add(attachment);
            attachmentRepository.save(attachment);

            return attachment;
        } else {
            throw new IOException("MedicalRecord not found");
        }
    }

    private Long getDoctorIdByUserId() {
        Long userId = AuthUtils.getCurrentUserId();
        boolean isDoctor = AuthUtils.isDoctor();

        if (!isDoctor) {
            logger.warn("Unauthorized access, isDoctor: {}", isDoctor);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND))
                .getId();
    }

    public byte[] downloadFileFromIpfs(Long recordId, Long fileId) throws IOException {
        MedicalRecord record = checkCanAccessRecord(recordId);

        if (record == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Optional<Attachment> attachmentOptional = attachmentRepository.findById(fileId);
        if (attachmentOptional.isPresent()) {
            String ipfsHash = attachmentOptional.get().getIpfsHash();
            return ipfsService.getFile(ipfsHash)
                    .orElseThrow(() -> new IOException("File not found in IPFS with hash: " + ipfsHash));
        } else {
            throw new IOException("Attachment not found");
        }

    }

    public String getAttachmentNameById(Long fileId) {
        Optional<Attachment> attachmentOptional = attachmentRepository.findById(fileId);
        if (attachmentOptional.isPresent()) {
            return attachmentOptional.get().getFileName();
        } else {
            throw new BusinessException("Attachment not found", ErrorCode.ATTACHMENT_NOT_FOUND);
        }
    }

    public List<Attachment> getAttachmentsByRecordId(Long recordId) {
        MedicalRecord record = checkCanAccessRecord(recordId);

        if (record == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Long doctorId = getDoctorIdByUserId();

        boolean isCreator = record.getCreatorDoctor().getId().equals(doctorId);
        boolean isShared = sharePermissionService.isRecordSharedWithDoctor(recordId, doctorId);

        if (!isCreator && !isShared) {
            logger.warn("Unauthorized access attempt by doctor: {} for record: {}", doctorId, recordId);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        // Return the list of attachments
        return record.getAttachments();
    }

    private MedicalRecord checkCanAccessRecord(Long recordId) {
        String username = AuthUtils.getCurrentUsername();
        Long userId = AuthUtils.getCurrentUserId();
        boolean isDoctor = AuthUtils.isDoctor();

        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RECORD_NOT_FOUND));

        if (isDoctor) {
            Long doctorId = getDoctorIdByUserId();

            boolean isCreator = record.getCreatorDoctor().getId().equals(doctorId);
            boolean isShared = sharePermissionService.isRecordSharedWithDoctor(recordId, doctorId);

            if (!isCreator && !isShared) {
                logger.warn("Unauthorized access attempt by doctor: {} for record: {}", doctorId, recordId);
                return null;
            }
        } else {
            if (!record.getUser().getId().equals(userId)) {
                logger.warn("Unauthorized access attempt by user: {} for record: {}", username, record.getId());
                return null;
            }
        }

        return record;
    }

}
