package edu.capstone4.chaincode;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

@Contract(
        name = "MedicalRecordContract",
        info = @Info(
                title = "Medical Record Management",
                description = "A contract for managing patient medical records",
                version = "1.0.0",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "info@meditracker.capstone4.edu",
                        name = "Medical Tracker",
                        url = "https://meditracker.capstone4.edu")
        )
)
@Default
public class MedicalRecordContract implements ContractInterface {

    private final Genson genson = new Genson();

    private enum MedicalRecordErrors {
        RECORD_NOT_FOUND,
        RECORD_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        MedicalRecord[] records = new MedicalRecord[]{
                new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Annual check-up"),
                new MedicalRecord("record2", "2021-02-01", "Dr. Jones", "Clinic B", "Follow-up visit")
        };

        MedicalInfo medicalInfo = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", records);
        String json = genson.serialize(medicalInfo);
        stub.putStringState("001", json);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String createMedicalInfo(final Context ctx, String patientId, String patientName,
                                    String patientGender, String patientEmail, MedicalRecord[] records) {
        ChaincodeStub stub = ctx.getStub();

        if (medicalRecordExists(ctx, patientId)) {
            String errorMessage = String.format("Medical Record for Patient ID %s already exists", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_ALREADY_EXISTS.toString());
        }

        MedicalInfo medicalInfo = new MedicalInfo(patientId, patientName, patientGender, patientEmail, records);
        String medicalInfoJson = genson.serialize(medicalInfo);
        stub.putStringState(patientId, medicalInfoJson);

        return medicalInfoJson;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public MedicalInfo readMedicalInfo(final Context ctx, final String patientId) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        return genson.deserialize(medicalInfoJson, MedicalInfo.class);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String updateMedicalInfo(final Context ctx, String patientId, String patientName,
                                    String patientGender, String patientEmail, MedicalRecord[] records) {
        ChaincodeStub stub = ctx.getStub();

        if (!medicalRecordExists(ctx, patientId)) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo updatedInfo = new MedicalInfo(patientId, patientName, patientGender, patientEmail, records);
        String updatedInfoJson = genson.serialize(updatedInfo);
        stub.putStringState(patientId, updatedInfoJson);

        return updatedInfoJson;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteMedicalInfo(final Context ctx, final String patientId) {
        ChaincodeStub stub = ctx.getStub();

        if (!medicalRecordExists(ctx, patientId)) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        stub.delState(patientId);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean medicalRecordExists(final Context ctx, final String patientId) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        return (medicalInfoJson != null && !medicalInfoJson.isEmpty());
    }
}
