//package edu.capstone4.userserver.controllers;
//
//import edu.capstone4.userserver.models.Doctor;
//import edu.capstone4.userserver.services.DoctorService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/doctors")
//public class DoctorController {
//
//    private final DoctorService doctorService;
//
//    @Autowired
//    public DoctorController(DoctorService doctorService) {
//        this.doctorService = doctorService;
//    }
//
//    // 注册医生
//    @PostMapping("/register")
//    public ResponseEntity<?> registerDoctor(@RequestBody Doctor doctor) {
//        try {
//            Doctor savedDoctor = doctorService.registerDoctor(doctor);
//            return ResponseEntity.status(HttpStatus.CREATED).body(savedDoctor);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error occurred while registering doctor: " + e.getMessage());
//        }
//    }
//
//    // 根据ID获取医生信息
//    @GetMapping("/{id}")
//    public ResponseEntity<Object> getDoctorById(@PathVariable Long id) {
//        return doctorService.findDoctorById(id)
//                .map(doctor -> ResponseEntity.ok().body((Object) doctor))
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body((Object) "Doctor not found"));
//    }
//
//    // 更新医生信息
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
//        Optional<Doctor> doctorOptional = doctorService.findDoctorById(id);
//        if (doctorOptional.isPresent()) {
//            Doctor existingDoctor = doctorOptional.get();
//            // 更新医生信息
//            existingDoctor.setProfessionalId(doctorDetails.getProfessionalId());
//            existingDoctor.setPersonalId(doctorDetails.getPersonalId());
//            existingDoctor.setLicense(doctorDetails.getLicense());
//            existingDoctor.setLicenseAuthority(doctorDetails.getLicenseAuthority());
//            existingDoctor.setJobTitle(doctorDetails.getJobTitle());
//            existingDoctor.setSpecialization(doctorDetails.getSpecialization());
//            existingDoctor.setClinicName(doctorDetails.getClinicName());
//            existingDoctor.setMembership(doctorDetails.getMembership());
//
//            Doctor updatedDoctor = doctorService.registerDoctor(existingDoctor);
//            return ResponseEntity.ok(updatedDoctor);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found");
//        }
//    }
//
//    // 删除医生
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
//        Optional<Doctor> doctorOptional = doctorService.findDoctorById(id);
//        if (doctorOptional.isPresent()) {
//            doctorService.deleteDoctorById(id);
//            return ResponseEntity.ok().body("Doctor deleted successfully");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found");
//        }
//    }
//
//}
