package edu.capstone4.userserver.controllers;

import edu.capstone4.userserver.exception.BusinessException;
import edu.capstone4.userserver.exception.ErrorCode;
import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DoctorRepository doctorRepository;

    // Admin activates doctor account
    @PutMapping("/activate-doctor/{doctorId}")
    public ResponseEntity<?> activateDoctor(@PathVariable Long doctorId) {
        // 获取Doctor对象
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);
        if (doctorOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new BusinessException(ErrorCode.DOCTOR_NOT_FOUND));
        }

        Doctor doctor = doctorOptional.get();

        // 检查医生是否已经激活
        if (doctor.isActivated()) {
            return ResponseEntity
                    .badRequest()
                    .body(new BusinessException(ErrorCode.DOCTOR_ALREADY_ACTIVATED));
        }

        // 激活医生账户
        doctor.setActivated(true);
        doctorRepository.save(doctor);

        return ResponseEntity.ok("Doctor activated successfully.");
    }
}
