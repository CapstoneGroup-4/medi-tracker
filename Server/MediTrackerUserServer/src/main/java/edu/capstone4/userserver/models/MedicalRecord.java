package edu.capstone4.userserver.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "medical_records") // 确保数据库中有相应的表，或者删除注解
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // General Information
    @NotBlank(message = "Patient name cannot be blank")
    private String patientName;

    @NotBlank(message = "Gender cannot be blank")
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Temporal(TemporalType.DATE) // 确保对 Date 类型的字段加上 Temporal 注解
    private Date dateOfBirth;

    @NotBlank(message = "Record number cannot be blank")
    private String recordNo;

    @NotBlank(message = "SIN cannot be blank")
    private String sin;

    @NotBlank(message = "NIK cannot be blank")
    private String nik;

    // Diagnosis & Treatment
    @NotBlank(message = "Primary diagnosis cannot be blank")
    private String primaryDiagnosis;

    @NotNull(message = "Date of diagnosis is required")
    @Temporal(TemporalType.DATE)
    private Date dateOfDiagnosis;

    @Column(length = 1000)
    private String doctorsNotes;

    @Column(length = 1000)
    private String treatmentPlan;

    private String nextSteps;

    @NotBlank(message = "Treatment status cannot be blank")
    private String treatmentStatus;

    @NotBlank(message = "Physician name cannot be blank")
    @Size(max = 100, message = "Physician name must be at most 100 characters")
    private String physicianName;


    // Prescribed Medication
    @Size(max = 100, message = "Medication name must be at most 100 characters")
    private String medicationName;

    @Size(max = 50, message = "Dosage must be at most 50 characters")
    private String dosage;

    @Size(max = 50, message = "Frequency must be at most 50 characters")
    private String frequency;

    @Size(max = 50, message = "Duration must be at most 50 characters")
    private String duration;

    @Column(length = 500)
    private String instructions;

    @NotBlank(message = "Prescribing doctor cannot be blank")
    @Size(max = 100, message = "Prescribing doctor must be at most 100 characters")
    private String prescribingDoctor;

    // Logical Deletion
    private Boolean isDeleted = false;

    // 文件附件列表
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;

    // 添加患者和医生的引用
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Getters and Setters 保留

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo;
    }

    public String getSin() {
        return sin;
    }

    public void setSin(String sin) {
        this.sin = sin;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getPrimaryDiagnosis() {
        return primaryDiagnosis;
    }

    public void setPrimaryDiagnosis(String primaryDiagnosis) {
        this.primaryDiagnosis = primaryDiagnosis;
    }

    public Date getDateOfDiagnosis() {
        return dateOfDiagnosis;
    }

    public void setDateOfDiagnosis(Date dateOfDiagnosis) {
        this.dateOfDiagnosis = dateOfDiagnosis;
    }

    public String getDoctorsNotes() {
        return doctorsNotes;
    }

    public void setDoctorsNotes(String doctorsNotes) {
        this.doctorsNotes = doctorsNotes;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getNextSteps() {
        return nextSteps;
    }

    public void setNextSteps(String nextSteps) {
        this.nextSteps = nextSteps;
    }

    public String getTreatmentStatus() {
        return treatmentStatus;
    }

    public void setTreatmentStatus(String treatmentStatus) {
        this.treatmentStatus = treatmentStatus;
    }

    public String getPhysicianName() {
        return physicianName;
    }

    public void setPhysicianName(String physicianName) {
        this.physicianName = physicianName;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getPrescribingDoctor() {
        return prescribingDoctor;
    }

    public void setPrescribingDoctor(String prescribingDoctor) {
        this.prescribingDoctor = prescribingDoctor;
    }


    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
