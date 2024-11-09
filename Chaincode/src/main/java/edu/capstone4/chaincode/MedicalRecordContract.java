package edu.capstone4.chaincode;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Contract(
        name = "MediTracker",
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

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String addMedicalRecord(final Context ctx, String patientId, MedicalRecord newRecord) {
        ChaincodeStub stub = ctx.getStub();

        // 检查患者信息是否存在
        String medicalInfoJson = stub.getStringState(patientId);
        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        // 反序列化现有的医疗信息
        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);

        // 获取当前的医疗记录数组，并将新记录添加到数组中
        MedicalRecord[] existingRecords = medicalInfo.getMedicalRecords();
        MedicalRecord[] updatedRecords = Arrays.copyOf(existingRecords, existingRecords.length + 1);
        updatedRecords[existingRecords.length] = newRecord;

        // 更新医疗信息对象
        medicalInfo = new MedicalInfo(medicalInfo.getPatientId(), medicalInfo.getPatientName(),
                medicalInfo.getPatientGender(), medicalInfo.getPatientEmail(), updatedRecords);

        // 序列化更新后的医疗信息并保存回账本
        medicalInfoJson = genson.serialize(medicalInfo);
        stub.putStringState(patientId, medicalInfoJson);

        return medicalInfoJson;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String updateMedicalRecord(final Context ctx, String patientId, String recordId, MedicalRecord updatedRecord) {
        ChaincodeStub stub = ctx.getStub();

        String medicalInfoJson = stub.getStringState(patientId);
        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);
        MedicalRecord[] records = medicalInfo.getMedicalRecords();

        boolean updated = false;
        for (int i = 0; i < records.length; i++) {
            if (records[i].getRecordID().equals(recordId)) {
                records[i] = updatedRecord;
                updated = true;
                break;
            }
        }

        if (!updated) {
            String errorMessage = String.format("Medical Record with ID %s not found", recordId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        medicalInfoJson = genson.serialize(medicalInfo);
        stub.putStringState(patientId, medicalInfoJson);

        return medicalInfoJson;
    }
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String deleteMedicalRecord(final Context ctx, String patientId, String recordId) {
        ChaincodeStub stub = ctx.getStub();

        String medicalInfoJson = stub.getStringState(patientId);
        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);
        List<MedicalRecord> recordList = new ArrayList<>(Arrays.asList(medicalInfo.getMedicalRecords()));
        boolean removed = recordList.removeIf(r -> r.getRecordID().equals(recordId));

        if (!removed) {
            String errorMessage = String.format("Medical Record with ID %s not found", recordId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalRecord[] updatedRecords = recordList.toArray(new MedicalRecord[0]);
        medicalInfo = new MedicalInfo(patientId, medicalInfo.getPatientName(), medicalInfo.getPatientGender(),
                medicalInfo.getPatientEmail(), updatedRecords);

        medicalInfoJson = genson.serialize(medicalInfo);
        stub.putStringState(patientId, medicalInfoJson);

        return medicalInfoJson;
    }
}
