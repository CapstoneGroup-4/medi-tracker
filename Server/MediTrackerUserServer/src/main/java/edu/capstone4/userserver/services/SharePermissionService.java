package edu.capstone4.userserver.services;

import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.models.MedicalRecord;
import edu.capstone4.userserver.models.SharePermission;
import edu.capstone4.userserver.repository.DoctorRepository;
import edu.capstone4.userserver.repository.MedicalRecordRepository;
import edu.capstone4.userserver.repository.SharePermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SharePermissionService {

    @Autowired
    private SharePermissionRepository sharePermissionRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public boolean isRecordSharedWithDoctor(Long recordId, Long doctorId) {
        return sharePermissionRepository.existsByMedicalRecordIdAndDoctorId(recordId, doctorId);
    }

    /**
     * Shares a medical record with a doctor.
     *
     * @param recordId   ID of the medical record to share.
     * @param doctorId   ID of the doctor with whom the record will be shared.
     * @return true if the record is successfully shared, false otherwise.
     */
    public boolean shareRecordWithDoctor(Long recordId, Long doctorId) {
        Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(recordId);
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);

        if (recordOpt.isPresent() && doctorOpt.isPresent()) {
            MedicalRecord record = recordOpt.get();
            Doctor doctor = doctorOpt.get();

            // Check if already shared
            if (sharePermissionRepository.existsByMedicalRecordAndDoctor(record, doctor)) {
                return false; // Record is already shared with this doctor
            }

            // Create and save new share permission
            SharePermission permission = new SharePermission();
            permission.setMedicalRecord(record);
            permission.setDoctor(doctor);
            permission.setSharedAt(new Date());
            sharePermissionRepository.save(permission);

            return true;
        }
        return false;
    }

    /**
     * Gets all doctors who have access to a specific medical record.
     *
     * @param recordId ID of the medical record.
     * @return List of doctors who have access to the record.
     */
    public List<Doctor> getDoctorsWithAccessToRecord(Long recordId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId).orElse(null);
        if (record == null) {
            return List.of(); // Return an empty list if the record does not exist
        }
        return sharePermissionRepository.findDoctorsByMedicalRecord(record);
    }

    /**
     * Revokes access for a specific doctor to a medical record.
     *
     * @param recordId ID of the medical record.
     * @param doctorId ID of the doctor whose access is to be revoked.
     * @return true if the access was successfully revoked, false otherwise.
     */
    public boolean revokeAccess(Long recordId, Long doctorId) {
        Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(recordId);
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);

        if (recordOpt.isPresent() && doctorOpt.isPresent()) {
            MedicalRecord record = recordOpt.get();
            Doctor doctor = doctorOpt.get();
            Optional<SharePermission> permission = sharePermissionRepository.findByMedicalRecordAndDoctor(record, doctor);

            if (permission.isPresent()) {
                sharePermissionRepository.delete(permission.get());
                return true;
            }
        }
        return false;
    }
}
