package edu.capstone4.userserver.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    private String fileName;
    private String fileType;

    // 添加 fileData 字段，用于存储文件的二进制数据
    @Lob
    private byte[] fileData;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    // 多对一关系，将每个附件关联到一个医疗记录
    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    public Attachment() {
    }

    public Attachment(String fileName, String fileType, byte[] fileData, Date uploadDate) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
        this.uploadDate = uploadDate;
    }

    // Getters and Setters
    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
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

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
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
