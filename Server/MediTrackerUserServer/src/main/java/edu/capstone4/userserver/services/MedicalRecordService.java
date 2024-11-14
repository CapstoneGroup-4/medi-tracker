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
import edu.capstone4.userserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
public class MedicalRecordService {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

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

    // Fetch paginated medical records sorted by last update date
    public Page<MedicalRecord> getAllRecords(Pageable pageable) {
        logger.info("Fetching all medical records with pagination, sorted by last update.");
        return medicalRecordRepository.findAll(pageable);
    }

    // Fetch a single medical record by its ID
    public MedicalRecord getRecordById(Long id) {
        logger.info("Fetching medical record with ID: {}", id);
        Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(id);
        if (recordOpt.isPresent()) {
            return recordOpt.get();
        } else {
            throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }
    }

    // Update an existing medical record by its ID
    public MedicalRecord updateRecord(Long id, MedicalRecordUpdateRequest updatedRecordRequest, Long doctorId) {
        logger.info("Updating medical record with ID: {}", id);

        // Find the doctor by doctorId
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND));

        return medicalRecordRepository.findById(id)
                .map(record -> {
                    if (!record.getCreatorDoctor().getId().equals(doctorId)) {
                        throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
                    }
                    record.setPrimaryDiagnosis(updatedRecordRequest.getPrimaryDiagnosis());
                    record.setDateOfDiagnosis(updatedRecordRequest.getDateOfDiagnosis());
                    record.setComment(updatedRecordRequest.getComment());
                    record.setDateOfLastUpdate(new Date());
                    record.setRecordVersion(record.getRecordVersion() + 1); // Increment version
                    return medicalRecordRepository.save(record);
                }).orElse(null);
    }

    // Delete (soft delete) a medical record by setting a deleted flag or removing it completely
    public void deleteRecord(Long id) {
        logger.info("Deleting medical record with ID: {}", id);
        Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(id);
        if (recordOpt.isPresent()) {
            MedicalRecord record = recordOpt.get();
            record.setDeleted(true);
            medicalRecordRepository.save(record);
            logger.info("Medical record with ID: {} has been marked as deleted.", id);
        } else {
            new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }
    }

    public Attachment uploadFileToIpfs(Long recordId, MultipartFile file) throws IOException {
        Optional<MedicalRecord> recordOptional = medicalRecordRepository.findById(recordId);
        if (recordOptional.isPresent()) {
            MedicalRecord record = recordOptional.get();

            // Upload to IPFS
            String ipfsHash = ipfsService.uploadFile(file);

            // Create Attachment with IPFS hash
            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileType(file.getContentType());
            attachment.setIpfsHash(ipfsHash);
            attachment.setUploadDate(new Date());
            attachment.setMedicalRecord(record);

            record.getAttachments().add(attachment);
            attachmentRepository.save(attachment);

            return attachment;
        } else {
            throw new IOException("MedicalRecord not found");
        }
    }

    public byte[] downloadFileFromIpfs(Long fileId) throws IOException {
        Optional<Attachment> attachmentOptional = attachmentRepository.findById(fileId);
        if (attachmentOptional.isPresent()) {
            String ipfsHash = attachmentOptional.get().getIpfsHash();
            return ipfsService.getFile(ipfsHash)
                    .orElseThrow(() -> new IOException("File not found in IPFS with hash: " + ipfsHash));
        } else {
            throw new IOException("Attachment not found");
        }
    }
}
