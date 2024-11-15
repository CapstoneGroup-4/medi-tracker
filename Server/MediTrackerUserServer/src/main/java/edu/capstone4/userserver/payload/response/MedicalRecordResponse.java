package edu.capstone4.userserver.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"creatorDoctor", "user", "sharedWithDoctors"})
public class MedicalRecordResponse {
    private Long id;

    private String patientName;

    private int gender;

    private int age;

    private String sin;

    private String recordId;

    private String primaryDiagnosis;

    private String dateOfDiagnosis;

    private String comment;

    private String doctorName;

    private String clinicName;

    public MedicalRecordResponse() {
    }

    public MedicalRecordResponse(Long id, String patientName, int gender, int age, String sin, String recordId, String primaryDiagnosis,
                                 String dateOfDiagnosis, String comment, String doctorName, String clinicName) {
        this.id = id;
        this.patientName = patientName;
        this.gender = gender;
        this.age = age;
        this.sin = sin;
        this.recordId = recordId;
        this.primaryDiagnosis = primaryDiagnosis;
        this.dateOfDiagnosis = dateOfDiagnosis;
        this.comment = comment;
        this.doctorName = doctorName;
        this.clinicName = clinicName;
    }

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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSin() {
        return sin;
    }

    public void setSin(String sin) {
        this.sin = sin;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getPrimaryDiagnosis() {
        return primaryDiagnosis;
    }

    public void setPrimaryDiagnosis(String primaryDiagnosis) {
        this.primaryDiagnosis = primaryDiagnosis;
    }

    public String getDateOfDiagnosis() {
        return dateOfDiagnosis;
    }

    public void setDateOfDiagnosis(String dateOfDiagnosis) {
        this.dateOfDiagnosis = dateOfDiagnosis;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }
}
