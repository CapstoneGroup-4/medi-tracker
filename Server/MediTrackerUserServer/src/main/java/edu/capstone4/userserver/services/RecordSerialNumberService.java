package edu.capstone4.userserver.services;

import edu.capstone4.userserver.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordSerialNumberService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Transactional
    public String generateSerialNumber() {
        // Retrieve the latest serial number
        Long count = medicalRecordRepository.count();
        return String.format("MEDI-TRACKER-REC-%06d", count + 1); // e.g., REC-000001
    }
}
