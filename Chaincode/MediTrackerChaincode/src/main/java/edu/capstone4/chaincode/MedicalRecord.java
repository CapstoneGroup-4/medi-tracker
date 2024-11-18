package edu.capstone4.chaincode;

import java.util.Objects;
import java.util.Set; // import Set

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class MedicalRecord {

    @Property()
    private String recordId;

    @Property()
    private String date;

    @Property()
    private String doctorId; // doctor -> doctorId

    @Property()
    private String clinic;

    @Property()
    private String detail;

    @Property()
    private int recordVersion; //new

    @Property()
    private String comment; //new

    @Property()
    private Set<String> permissions; // ArrayList -> Set

    @Property()
    private String ipfsFileId; // new field: ipfsFileId

    public MedicalRecord(@JsonProperty("recordId") String recordId,
                         @JsonProperty("date") String date,
                         @JsonProperty("doctorId") String doctorId, // doctor -> doctorId
                         @JsonProperty("clinic") String clinic,
                         @JsonProperty("detail") String detail,
                         @JsonProperty("recordVersion") int recordVersion,
                         @JsonProperty("comment") String comment,
                         @JsonProperty("permissions") Set<String> permissions, // ArrayList -> Set
                         @JsonProperty("ipfsFileId") String ipfsFileId) { // new field: ipfsFileId
        this.recordId = recordId;
        this.date = date;
        this.doctorId = doctorId; // doctor -> doctorId
        this.clinic = clinic;
        this.detail = detail;
        this.recordVersion = recordVersion; // new
        this.comment = comment; // new
        this.permissions = permissions; // ArrayList -> Set
        this.ipfsFileId = ipfsFileId; // new field: ipfsFileId
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(int recordVersion) {
        this.recordVersion = recordVersion;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getIpfsFileId() {
        return ipfsFileId;
    }

    @Override
    // no comparisons of versions, comments and permissions
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MedicalRecord that = (MedicalRecord) o;
        return Objects.equals(recordId, that.recordId) &&
                Objects.equals(date, that.date) &&
                Objects.equals(doctorId, that.doctorId) && // doctor -> doctorId
                Objects.equals(clinic, that.clinic) &&
                Objects.equals(detail, that.detail) &&
                Objects.equals(ipfsFileId, that.ipfsFileId);
    }

    @Override
    // no comparisons of versions, comments and permissions
    // included ipfsFileId in hashCode (new)
    public int hashCode() {
        int result = Objects.hash(recordId, date, doctorId, clinic, detail, recordVersion, comment, ipfsFileId); // added ipfsFileId
        result = 31 * result;
        return result;
    }

    @Override
    // no permissions
    // included ipfsFileId in toString (new)
    public String toString() {
        return "MedicalRecord{" +
                "recordID='" + recordId + '\'' +
                ", date='" + date + '\'' +
                ", doctorId='" + doctorId + '\'' + // doctor -> doctorId
                ", clinic='" + clinic + '\'' +
                ", detail='" + detail + '\'' +
                ", recordVersion=" + recordVersion +
                ", comment='" + comment + '\'' +
                ", ipfsFileId='" + ipfsFileId + '\'' + // included ipfsFileId
                '}';
    }
}
