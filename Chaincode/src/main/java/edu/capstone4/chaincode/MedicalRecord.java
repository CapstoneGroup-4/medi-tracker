package edu.capstone4.chaincode;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class MedicalRecord {

    @Property()
    private final String recordID;

    @Property()
    private final String date;

    @Property()
    private final String doctor;

    @Property()
    private final String clinic;

    @Property()
    private final String detail; // 新增的详情字段

    public MedicalRecord(String recordID, String date, String doctor, String clinic, String detail) {
        this.recordID = recordID;
        this.date = date;
        this.doctor = doctor;
        this.clinic = clinic;
        this.detail = detail; // 初始化新字段
    }

    public String getRecordID() {
        return recordID;
    }

    public String getDate() {
        return date;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getClinic() {
        return clinic;
    }

    public String getDetail() {
        return detail; // Getter for the new detail field
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalRecord that = (MedicalRecord) o;
        return Objects.equals(recordID, that.recordID) &&
                Objects.equals(date, that.date) &&
                Objects.equals(doctor, that.doctor) &&
                Objects.equals(clinic, that.clinic) &&
                Objects.equals(detail, that.detail); // Include detail in equality check
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordID, date, doctor, clinic, detail); // Include detail in hash code
    }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "recordID='" + recordID + '\'' +
                ", date='" + date + '\'' +
                ", doctor='" + doctor + '\'' +
                ", clinic='" + clinic + '\'' +
                ", detail='" + detail + '\'' + // Include detail in string representation
                '}';
    }
}
