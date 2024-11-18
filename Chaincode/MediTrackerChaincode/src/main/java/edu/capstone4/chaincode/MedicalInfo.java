package edu.capstone4.chaincode;

import java.util.Objects;
import java.util.Set;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class MedicalInfo {

    @Property()
    private String patientId;

    @Property()
    private String patientName;

    @Property()
    private String patientGender;

    @Property()
    private String patientEmail;

    @Property()
    private int medicalVersion; //new, record updates version

    @Property()
    private String comment; //new, record updates version

    @Property()
    private String patientPrivateKey; //new, permission control

    @Property()
    private Set<MedicalRecord> medicalRecords; // arraylist -> Set


    public MedicalInfo(@JsonProperty("patientId") String patientId,
                       @JsonProperty("patientName") String patientName,
                       @JsonProperty("patientGender") String patientGender,
                       @JsonProperty("patientEmail") String patientEmail,
                       @JsonProperty("medicalVersion") int medicalVersion,
                       @JsonProperty("comment") String comment,
                       @JsonProperty("patientPrivateKey") String patientPrivateKey,
                       @JsonProperty("medicalRecords") Set<MedicalRecord> medicalRecords) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientGender = patientGender;
        this.patientEmail = patientEmail;
        this.medicalVersion = medicalVersion; // new
        this.comment = comment; // new
        this.patientPrivateKey = patientPrivateKey; //new
        this.medicalRecords = medicalRecords;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public int getMedicalVersion() {
        return medicalVersion;
    }

    public void setMedicalVersion(int medicalVersion) {
        this.medicalVersion = medicalVersion;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPatientPrivateKey() {
        return patientPrivateKey;
    }

    public void setPatientPrivateKey(String patientPrivateKey) {
        this.patientPrivateKey = patientPrivateKey;
    }

    public Set<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(Set<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    @Override
    // no comparisons of versions, comments, private keys
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MedicalInfo that = (MedicalInfo) o;
        return Objects.equals(patientId, that.patientId) &&
                Objects.equals(patientName, that.patientName) &&
                Objects.equals(patientGender, that.patientGender) &&
                Objects.equals(patientEmail, that.patientEmail) &&
                Objects.equals(medicalRecords, that.medicalRecords);
    }


    @Override
    // no comparisons of versions, comments, private keys
    public int hashCode() {
        int result = Objects.hash(patientId, patientName, patientGender, medicalRecords);
        result = 31 * result;
        return result;
    }

    @Override
    // no private keys
    public String toString() {
        return "MedicalInfo{" +
                "patientId='" + patientId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientGender='" + patientGender + '\'' +
                ", patientEmail='" + patientEmail + '\'' +
                ", medicalVersion=" + medicalVersion +
                ", comment='" + comment + '\'' +
                ", medicalRecords=" + medicalRecords +
                '}';
    }
}
