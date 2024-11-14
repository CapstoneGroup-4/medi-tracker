package edu.capstone4.userserver.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;

    // Stores the IPFS hash for locating the file in IPFS
    private String ipfsHash;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    // Many-to-One relationship with MedicalRecord
    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    public Attachment() {
    }

    public Attachment(String fileName, String fileType, String ipfsHash, Date uploadDate) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.ipfsHash = ipfsHash;
        this.uploadDate = uploadDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getIpfsHash() {
        return ipfsHash;
    }

    public void setIpfsHash(String ipfsHash) {
        this.ipfsHash = ipfsHash;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }
}
