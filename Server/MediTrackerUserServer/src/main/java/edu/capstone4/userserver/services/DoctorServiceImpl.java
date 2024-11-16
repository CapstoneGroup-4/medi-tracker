//package edu.capstone4.userserver.services;
//
//import edu.capstone4.userserver.models.Doctor;
//import edu.capstone4.userserver.repository.DoctorRepository;
//import edu.capstone4.userserver.services.DoctorService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class DoctorServiceImpl implements DoctorService {
//
//    @Autowired
//    private DoctorRepository doctorRepository;
//
//    @Override
//    public Doctor registerDoctor(Doctor doctor) {
//        if (doctorRepository.existsByPersonalId(doctor.getPersonalId())) {
//            throw new RuntimeException("Doctor with personalId " + doctor.getPersonalId() + " already exists.");
//        }
//        return doctorRepository.save(doctor);
//    }
//
//    @Override
//    public Optional<Doctor> findDoctorById(Long id) {
//        return doctorRepository.findById(id);
//    }
//
//    @Override
//    public Optional<Doctor> findDoctorByPersonalId(String personalId) {
//        return doctorRepository.findByPersonalId(personalId);
//    }
//
//    @Override
//    public void deleteDoctorById(Long id) {
//        doctorRepository.deleteById(id); // 实现删除医生的方法
//    }
//}
