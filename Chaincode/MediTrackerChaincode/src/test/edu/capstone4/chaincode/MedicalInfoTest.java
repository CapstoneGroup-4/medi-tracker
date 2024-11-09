package org.hyperledger.fabric.samples.medicalrecords;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class MedicalInfoTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            MedicalRecord[] records = {new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Annual check-up")};
            MedicalInfo medicalInfo = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);

            assertThat(medicalInfo).isEqualTo(medicalInfo);
        }

        @Test
        public void isSymmetric() {
            MedicalRecord[] records = {new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Annual check-up")};
            MedicalInfo medicalInfoA = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);
            MedicalInfo medicalInfoB = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);

            assertThat(medicalInfoA).isEqualTo(medicalInfoB);
            assertThat(medicalInfoB).isEqualTo(medicalInfoA);
        }

        @Test
        public void isTransitive() {
            MedicalRecord[] records = {new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Annual check-up")};
            MedicalInfo medicalInfoA = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);
            MedicalInfo medicalInfoB = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);
            MedicalInfo medicalInfoC = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);

            assertThat(medicalInfoA).isEqualTo(medicalInfoB);
            assertThat(medicalInfoB).isEqualTo(medicalInfoC);
            assertThat(medicalInfoA).isEqualTo(medicalInfoC);
        }

        @Test
        public void handlesInequality() {
            MedicalRecord[] recordsA = {new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Annual check-up")};
            MedicalRecord[] recordsB = {new MedicalRecord("record2", "2021-02-01", "Dr. Jones", "Clinic B", "Follow-up visit")};
            MedicalInfo medicalInfoA = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", recordsA);
            MedicalInfo medicalInfoB = new MedicalInfo("002", "Jane Smith", "F", "jane.smith@example.com", recordsB);

            assertThat(medicalInfoA).isNotEqualTo(medicalInfoB);
        }

        @Test
        public void handlesOtherObjects() {
            MedicalRecord[] records = {new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Annual check-up")};
            MedicalInfo medicalInfo = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);
            String notAMedicalInfo = "not a medical info";

            assertThat(medicalInfo).isNotEqualTo(notAMedicalInfo);
        }

        @Test
        public void handlesNull() {
            MedicalRecord[] records = {new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Annual check-up")};
            MedicalInfo medicalInfo = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);

            assertThat(medicalInfo).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesMedicalInfo() {
        MedicalRecord[] records = {new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Annual check-up")};
        MedicalInfo medicalInfo = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);

        assertThat(medicalInfo.toString()).isEqualTo("MedicalInfo{patientId='001', patientName='John Doe', patientGender='M', patientEmail='john.doe@example.com', medicalRecords=[MedicalRecord{recordID='record1', date='2021-01-01', doctor='Dr. Smith', clinic='Clinic A', detail='Annual check-up'}]}");
    }
}
