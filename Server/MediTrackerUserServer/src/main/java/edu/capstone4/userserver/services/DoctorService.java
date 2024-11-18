package edu.capstone4.userserver.services;

import edu.capstone4.userserver.models.Doctor;
import edu.capstone4.userserver.models.User;
import edu.capstone4.userserver.repository.DoctorRepository;
import edu.capstone4.userserver.repository.UserRepository;
import edu.capstone4.userserver.exceptions.BusinessException;
import edu.capstone4.userserver.exceptions.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND));
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND));
    }

    // 保存医生信息
    public Doctor saveDoctor(Doctor doctor, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (doctorRepository.existsByPersonalId(doctor.getPersonalId())) {
            throw new BusinessException(ErrorCode.DOCTOR_EXIST);
        }

        // 关联 User 和 Doctor
        doctor.setUser(user);
        return doctorRepository.save(doctor);
    }

    // 激活医生账户
    public void activateDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCTOR_NOT_FOUND));

        if (doctor.isActivated()) {
            throw new BusinessException(ErrorCode.DOCTOR_ALREADY_ACTIVATED);
        }

        doctor.setActivated(true);
        doctorRepository.save(doctor);
    }
}
