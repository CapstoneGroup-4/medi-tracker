package org.hyperledger.fabric.samples.medicalrecords;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public final class MedicalRecordContractTest {

    @Nested
    class MedicalRecordOperations {

        @Test
        public void whenMedicalInfoExists() {
            MedicalRecordContract contract = new MedicalRecordContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            MedicalRecord[] records = {new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Check-up")};
            when(stub.getStringState("001")).thenReturn("{ \"patientId\": \"001\", \"patientName\": \"John Doe\", \"patientGender\": \"M\", \"patientEmail\": \"johndoe@example.com\", \"medicalRecords\": [{ \"recordID\": \"record1\", \"date\": \"2021-01-01\", \"doctor\": \"Dr. Smith\", \"clinic\": \"Clinic A\", \"detail\": \"Check-up\" }] }");

            MedicalInfo medicalInfo = contract.readMedicalInfo(ctx, "001");

            assertThat(medicalInfo).isEqualTo(new MedicalInfo("001", "John Doe", "M", "johndoe@example.com", records));
        }

        @Test
        public void whenMedicalInfoDoesNotExist() {
            MedicalRecordContract contract = new MedicalRecordContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("001")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.readMedicalInfo(ctx, "001");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class)
                    .hasMessageContaining("Medical Record for Patient ID 001 does not exist");
        }

        @Test
        public void createMedicalInfoSuccess() {
            MedicalRecordContract contract = new MedicalRecordContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("001")).thenReturn("");
            MedicalRecord[] records = {new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Check-up")};

            MedicalInfo createdInfo = contract.createMedicalInfo(ctx, "001", "John Doe", "M", "johndoe@example.com", records);
            assertThat(createdInfo.getPatientName()).isEqualTo("John Doe");
            Mockito.verify(stub).putStringState("001", "{\"patientId\":\"001\",\"patientName\":\"John Doe\",\"patientGender\":\"M\",\"patientEmail\":\"johndoe@example.com\",\"medicalRecords\":[{\"recordID\":\"record1\",\"date\":\"2021-01-01\",\"doctor\":\"Dr. Smith\",\"clinic\":\"Clinic A\",\"detail\":\"Check-up\"}]}");
        }

        @Test
        public void deleteMedicalInfoSuccess() {
            MedicalRecordContract contract = new MedicalRecordContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("001")).thenReturn("{ \"patientId\": \"001\" }");  // Simplified for example

            contract.deleteMedicalInfo(ctx, "001");
            Mockito.verify(stub).delState("001");
        }
    }

    @Nested
    class ErrorHandling {

        @Test
        public void errorWhenDeletingNonexistentInfo() {
            MedicalRecordContract contract = new MedicalRecordContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("001")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.deleteMedicalInfo(ctx, "001");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class)
                    .hasMessageContaining("Medical Record for Patient ID 001 does not exist");
        }
    }
}
