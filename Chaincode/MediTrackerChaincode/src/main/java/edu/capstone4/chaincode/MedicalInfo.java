package edu.capstone4.chaincode;

import java.util.Arrays;
import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class MedicalInfo {

    @Property()
    private final String patientId;

    @Property()
    private final String patientName;

    @Property()
    private final String patientGender;

    @Property()
    private final String patientEmail;

    @Property()
    private final MedicalRecord[] medicalRecords;

    public MedicalInfo() {
        this.patientId = "";
        this.patientName = "";
        this.patientGender = "";
        this.patientEmail = "";
        this.medicalRecords = new MedicalRecord[0];
    }


    public MedicalInfo(@JsonProperty("patientId") String patientId,
                       @JsonProperty("patientName") String patientName,
                       @JsonProperty("patientGender") String patientGender,
                       @JsonProperty("patientEmail") String patientEmail,
                       @JsonProperty("medicalRecords") MedicalRecord[] medicalRecords) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientGender = patientGender;
        this.patientEmail = patientEmail;
        this.medicalRecords = medicalRecords;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public MedicalRecord[] getMedicalRecords() {
        return medicalRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalInfo that = (MedicalInfo) o;
        return Objects.equals(patientId, that.patientId) &&
                Objects.equals(patientName, that.patientName) &&
                Objects.equals(patientGender, that.patientGender) &&
                Objects.equals(patientEmail, that.patientEmail) &&
                Arrays.equals(medicalRecords, that.medicalRecords);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(patientId, patientName, patientGender, patientEmail);
        result = 31 * result + Arrays.hashCode(medicalRecords);
        return result;
    }

    @Override
    public String toString() {
        return "MedicalInfo{" +
                "patientId='" + patientId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientGender='" + patientGender + '\'' +
                ", patientEmail='" + patientEmail + '\'' +
                ", medicalRecords=" + Arrays.toString(medicalRecords) +
                '}';
    }
}
