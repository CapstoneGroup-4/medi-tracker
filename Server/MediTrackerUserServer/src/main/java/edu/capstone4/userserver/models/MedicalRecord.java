package edu.capstone4.userserver.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_doctor_id", referencedColumnName = "id", nullable = false)
    private Doctor creatorDoctor;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    private String recordSerialNumber; // Unique identifier for linking with chaincode

    private String clinicName;

    private String primaryDiagnosis; // Primary condition diagnosed

    private String dateOfDiagnosis; // Date of diagnosis

    private String comment; // Additional notes or comments

    private int recordVersion; // For tracking updates

    private boolean isDeleted = false; // For soft delete

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attachment> attachments; // Associated attachments

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SharePermission> sharedWithDoctors = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfLastUpdate;

    public MedicalRecord() {
        this.dateOfCreate = new Date(); // Set creation date at instantiation
        this.dateOfLastUpdate = new Date(); // Set last update date at instantiation
        this.attachments = new ArrayList<>(); // Initialize as empty list
        this.sharedWithDoctors = new ArrayList<>(); // Initialize as empty list
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getCreatorDoctor() {
        return creatorDoctor;
    }

    public void setCreatorDoctor(Doctor creatorDoctor) {
        this.creatorDoctor = creatorDoctor;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRecordSerialNumber() {
        return recordSerialNumber;
    }

    public void setRecordSerialNumber(String recordSerialNumber) {
        this.recordSerialNumber = recordSerialNumber;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
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

    public Date getDateOfLastUpdate() {
        return dateOfLastUpdate;
    }

    public void setDateOfLastUpdate(Date dateOfLastUpdate) {
        this.dateOfLastUpdate = dateOfLastUpdate;
    }

    public Date getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(Date dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(int recordVersion) {
        this.recordVersion = recordVersion;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<SharePermission> getSharedWithDoctors() {
        return sharedWithDoctors;
    }

    public void setSharedWithDoctors(List<SharePermission> sharedWithDoctors) {
        this.sharedWithDoctors = sharedWithDoctors;
    }
}
