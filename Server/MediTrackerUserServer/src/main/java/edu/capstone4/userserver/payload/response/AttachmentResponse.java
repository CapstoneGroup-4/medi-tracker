package edu.capstone4.userserver.payload.response;

import java.util.Date;

public class AttachmentResponse {
    private Long attachmentId;

    private String attachmentName;

    private String ipfsHash;

    private Long recordId;

    private String recordNo;

    private Date uploadDate;

    public AttachmentResponse() {
    }

    public AttachmentResponse(Long attachmentId, String attachmentName, String ipfsHash, Long recordId, String recordNo, Date uploadDate) {
        this.attachmentId = attachmentId;
        this.attachmentName = attachmentName;
        this.ipfsHash = ipfsHash;
        this.recordId = recordId;
        this.recordNo = recordNo;
        this.uploadDate = uploadDate;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getIpfsHash() {
        return ipfsHash;
    }

    public void setIpfsHash(String ipfsHash) {
        this.ipfsHash = ipfsHash;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}
