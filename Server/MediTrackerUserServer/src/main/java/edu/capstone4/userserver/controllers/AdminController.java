package edu.capstone4.userserver.controllers;

import edu.capstone4.userserver.exceptions.ErrorCode;
import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.payload.response.BaseResponse;
import edu.capstone4.userserver.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DoctorRepository doctorRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    // Admin activates doctor account
    @PutMapping("/activate-doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateDoctor(@PathVariable Long doctorId) {
        logger.info("Accessing activateDoctor with role: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        // 获取Doctor对象
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);
        if (doctorOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(ErrorCode.DOCTOR_NOT_FOUND.getMessage(), ErrorCode.DOCTOR_NOT_FOUND.getCode()));
        }

        Doctor doctor = doctorOptional.get();

        // 检查医生是否已经激活
        if (doctor.isActivated()) {
            return ResponseEntity
                    .badRequest()
                    .body(new BaseResponse<>(ErrorCode.DOCTOR_ALREADY_ACTIVATED.getMessage(), ErrorCode.DOCTOR_ALREADY_ACTIVATED.getCode()));
        }

        // 激活医生账户
        doctor.setActivated(true);
        doctorRepository.save(doctor);

        return ResponseEntity.ok(new BaseResponse<>("Doctor activated successfully."));
    }
}
