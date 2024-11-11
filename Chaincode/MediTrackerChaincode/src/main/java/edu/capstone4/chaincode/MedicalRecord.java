package edu.capstone4.chaincode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
    private String doctor;

    @Property()
    private String clinic;

    @Property()
    private String detail;

    @Property()
    private int recordVersion; //new

    @Property()
    private String comment; //new

    @Property()
    private ArrayList<String> permissions; //new

    public MedicalRecord(@JsonProperty("recordId") String recordId,
                         @JsonProperty("date") String date,
                         @JsonProperty("doctor") String doctor,
                         @JsonProperty("clinic") String clinic,
                         @JsonProperty("detail") String detail,
                         @JsonProperty("recordVersion") int recordVersion,
                         @JsonProperty("comment") String comment,
                         @JsonProperty("permissions") ArrayList<String> permissions) {
        this.recordId = recordId;
        this.date = date;
        this.doctor = doctor;
        this.clinic = clinic;
        this.detail = detail;
        this.recordVersion = recordVersion; // new
        this.comment = comment; // new
        this.permissions = permissions; // new
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

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
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

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
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
                Objects.equals(doctor, that.doctor) &&
                Objects.equals(clinic, that.clinic) &&
                Objects.equals(detail, that.detail);
    }

    @Override
    // no comparisons of versions, comments and permissions
    public int hashCode() {
        int result = Objects.hash(recordId, date, doctor, clinic, detail);
        result = 31 * result;
        return result;
    }

    @Override
    // no permissions
    public String toString() {
        return "MedicalRecord{" +
                "recordID='" + recordId + '\'' +
                ", date='" + date + '\'' +
                ", doctor='" + doctor + '\'' +
                ", clinic='" + clinic + '\'' +
                ", detail='" + detail + '\'' +
                ", recordVersion=" + recordVersion +
                ", comment='" + comment + '\'' +
                '}';
    }
}
