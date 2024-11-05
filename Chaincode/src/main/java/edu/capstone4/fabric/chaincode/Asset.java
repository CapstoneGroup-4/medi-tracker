/*
 * SPDX-License-Identifier: Apache-2.0
 * Author: Hong
 * Update Time: 2024.11.3
 * Version: 0
 */

package edu.capstone4.fabric.chaincode;

import java.util.Objects;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.annotation.JsonProperty;

// Importing ZonedDateTime and related classes for time management
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

@DataType()  // Declares this class as a data type for Hyperledger Fabric smart contracts
public final class Asset {

    @Property()
    // Unique identifier for the record (was assetID in the template)
    private final String recordID;

    @Property()
    // ID of the patient associated with this medical record (was color in the template)
    private final String patientID;

    @Property()
    // ID of the hospital associated with this record (new field)
    private final String hospitalID;

    @Property()
    // ID of the doctor associated with this record (new field)
    private final String doctorID;

    @Property()
    // URL to the medical record (new field)
    private final String recordURL;

    @Property()
    // Hash code of the medical record to ensure integrity (new field)
    private final String recordHashCode;

    @Property()
    // Version number of the asset (new field)
    private final int version;

    @Property()
    // Time when the record was last updated (new field)
    private final ZonedDateTime updateTime;

    // ----------------------------------------------------------
    // Getters for all fields (return values of private fields)

    public String getRecordID() {
        return recordID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getRecordURL() {
        return recordURL;
    }

    public String getRecordHashCode() {
        return recordHashCode;
    }

    public int getVersion() {
        return version;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    // Constructor to initialize all properties of the Asset object
    public Asset(
            @JsonProperty("recordID") final String recordID,
            @JsonProperty("patientID") final String patientID,
            @JsonProperty("hospitalID") final String hospitalID,
            @JsonProperty("doctorID") final String doctorID,
            @JsonProperty("recordURL") final String recordURL,
            @JsonProperty("recordHashCode") final String recordHashCode,
            @JsonProperty("version") final int version,
            @JsonProperty("updateTime") final ZonedDateTime updateTime) {
        this.recordID = recordID;
        this.patientID = patientID;
        this.hospitalID = hospitalID;
        this.doctorID = doctorID;
        this.recordURL = recordURL;
        this.recordHashCode = recordHashCode;
        this.version = version;
        this.updateTime = updateTime;
    }

    // Override equals method to compare Asset objects
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Asset other = (Asset) obj;

        // Check if all fields are deeply equal between this object and the other
        return Objects.deepEquals(
                new String[] {getRecordID(), getPatientID(), getHospitalID(), getDoctorID(), getRecordURL(), getRecordHashCode()},
                new String[] {other.getRecordID(), other.getPatientID(), other.getHospitalID(), other.getDoctorID(), other.getRecordURL(), other.getRecordHashCode()})
                &&
                Objects.deepEquals(
                        new int[] {getVersion()},
                        new int[] {other.getVersion()}) &&
                Objects.deepEquals(
                        new ZonedDateTime[] {getUpdateTime()},
                        new ZonedDateTime[] {other.getUpdateTime()});
    }

    // Override hashCode to generate a unique hash for each Asset object
    @Override
    public int hashCode() {
        return Objects.hash(getRecordID(), getPatientID(), getHospitalID(), getDoctorID(),
                getRecordURL(), getRecordHashCode(), getVersion(), getUpdateTime());
    }

    // Override toString to provide a readable string representation of the Asset object
    @Override
    public String toString() {
        // Format updateTime to a readable string format (e.g., "2024-11-03 10:30:00 GMT")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
        String formattedDateTime = updateTime.format(formatter);
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                " [recordID=" + recordID +
                ", patientID=" + patientID +
                ", hospitalID=" + hospitalID +
                ", doctorID=" + doctorID +
                ", recordURL=" + recordURL +
                ", recordHashCode=" + recordHashCode +
                ", version=" + version +
                ", updateTime=" + formattedDateTime + "]";
    }
}
