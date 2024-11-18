package edu.capstone4.chaincode;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

import java.util.*;

@Contract(
        name = "MediTracker",
        info = @Info(
                title = "Medical Record Management",
                description = "A contract for managing patient medical records",
                version = "1.0.1",
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
        RECORD_ALREADY_EXISTS,
        NO_PERMISSION
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    // only for testing
    public String initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Set<String> permission1 = new HashSet<>(Set.of("Dr.Smith_PrivateKey"));
        Set<String> permission2 = new HashSet<>(Set.of("Dr.Jones_PrivateKey"));

        Set<MedicalRecord> records = new HashSet<>();
        records.add(new MedicalRecord("record1", "2021-01-01", "Dr. Smith", "Clinic A", "Annual check-up", 0, "test", permission1, "field1"));
        records.add(new MedicalRecord("record2", "2021-02-01", "Dr. Jones", "Clinic B", "Follow-up visit", 0, "test", permission2, "field2"));

        MedicalInfo medicalInfo1 = new MedicalInfo("001", "John Doe", "M", "john.doe@example.com", 0,"test", "patient001__PrivateKey", records);
        MedicalInfo medicalInfo2 = new MedicalInfo("002", "Tom C", "M", "Tome.c@example.com", 0,"test", "patient002__PrivateKey", records);

        String json1 = genson.serialize(medicalInfo1);
        stub.putStringState("001", json1);
        String json2 = genson.serialize(medicalInfo2);
        stub.putStringState("002", json2);

        return medicalInfo1.toString() + '\n' + medicalInfo2.toString();

    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    // only the patient has access to all medical records
    public String readMedicalInfo(final Context ctx, final String patientId, final String patientPrivateKey) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);

        if(medicalInfo.getPatientPrivateKey().equals(patientPrivateKey)){
            return medicalInfo.toString();
        }else{
            String errorMessage = String.format("You have on permission of access to records for Patient ID %s", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.NO_PERMISSION.toString());
        }
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String createMedicalInfo(final Context ctx, String patientId, String patientName,
                                    String patientGender, String patientEmail, String patientPrivateKey,
                                    Set<MedicalRecord> records) {
        ChaincodeStub stub = ctx.getStub();

        if (medicalRecordExists(ctx, patientId)) {
            String errorMessage = String.format("Medical Record for Patient ID %s already exists", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_ALREADY_EXISTS.toString());
        }

        MedicalInfo medicalInfo = new MedicalInfo(patientId, patientName, patientGender, patientEmail, 0, "original",patientPrivateKey, records);
        String medicalInfoJson = genson.serialize(medicalInfo);
        stub.putStringState(patientId, medicalInfoJson);

        return medicalInfoJson;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    // only the patient has access to all medical records
    public String updateMedicalInfo(final Context ctx, String patientId, String patientName,
                                    String patientGender, String patientEmail,
                                    String patientPrivateKey, Set<MedicalRecord> records) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);

        if(medicalInfo.getPatientPrivateKey().equals(patientPrivateKey)){

            deleteMedicalInfo(ctx, patientId,patientPrivateKey);

            MedicalInfo updatedInfo = new MedicalInfo(patientId, patientName, patientGender, patientEmail,
                    medicalInfo.getMedicalVersion() + 1, "updated", patientPrivateKey,records);
            String updatedInfoJson = genson.serialize(updatedInfo);
            stub.putStringState(patientId, updatedInfoJson);

            return updatedInfoJson;
        }else{
            String errorMessage = String.format("You have on permission of access to records for Patient ID %s", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.NO_PERMISSION.toString());
        }

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    // only the patient has access to all medical records
    public void deleteMedicalInfo(final Context ctx, final String patientId, final String patientPrivateKey ) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);

        if(medicalInfo.getPatientPrivateKey().equals(patientPrivateKey)){

            stub.delState(patientId);

            String deletedPatientId = patientId + "_DELETED";
            medicalInfo.setPatientId(deletedPatientId);
            medicalInfo.setComment("deleted");

            String deletedInfoJson = genson.serialize(medicalInfo);
            stub.putStringState(deletedPatientId, deletedInfoJson);

        }else{
            String errorMessage = String.format("You have on permission of access to records for Patient ID %s", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.NO_PERMISSION.toString());
        }
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean medicalRecordExists(final Context ctx, final String patientId) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        return (medicalInfoJson != null && !medicalInfoJson.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String addMedicalRecord(final Context ctx, String patientId, final String permissionPrivateKey,MedicalRecord newRecord) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);

        if(medicalInfo.getMedicalRecords().contains(newRecord)){
            String errorMessage = String.format("Medical Record for Record ID %s already exists", newRecord.getRecordId());
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_ALREADY_EXISTS.toString());
        }

        if(medicalInfo.getPatientPrivateKey().equals(permissionPrivateKey)){

            medicalInfo.getMedicalRecords().add(newRecord);

            medicalInfoJson = genson.serialize(medicalInfo);
            stub.putStringState(patientId, medicalInfoJson);

            return medicalInfoJson;
        }else{
            String errorMessage = String.format("You have on permission of access to records for Patient ID %s", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.NO_PERMISSION.toString());
        }


    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    // only the patient has access to all medical records
    public MedicalRecord readMedicalRec(final Context ctx, String patientId, String recordId, final String permissionPrivateKey) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);

        for ( MedicalRecord record: medicalInfo.getMedicalRecords()) {
            if(record.getRecordId().equals(recordId)){
                if(medicalInfo.getPatientPrivateKey().equals(permissionPrivateKey) || record.getPermissions().contains(permissionPrivateKey)){
                    return record;
                }else {
                    String errorMessage = String.format("You have on permission of access to the record for Record ID %s", recordId);
                    throw new ChaincodeException(errorMessage, MedicalRecordErrors.NO_PERMISSION.toString());
                }
            }
        }

        String errorMessage = String.format("Medical Record for Record ID %s does not exist", recordId);
        throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String updateMedicalRecord(final Context ctx, String patientId, String recordId, String permissionPrivateKey,MedicalRecord updatedRecord) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);

        for ( MedicalRecord record: medicalInfo.getMedicalRecords()) {
            if(record.getRecordId().equals(recordId)){
                if(medicalInfo.getPatientPrivateKey().equals(permissionPrivateKey) || record.getPermissions().contains(permissionPrivateKey)){

                    deleteMedicalRecord(ctx,patientId,recordId,permissionPrivateKey);

                    updatedRecord.setRecordVersion(record.getRecordVersion() + 1);
                    updatedRecord.setComment("updated");

                    addMedicalRecord(ctx,patientId,permissionPrivateKey,updatedRecord);

                    return updatedRecord.toString();

                }else {
                    String errorMessage = String.format("You have on permission of access to the record for Record ID %s", recordId);
                    throw new ChaincodeException(errorMessage, MedicalRecordErrors.NO_PERMISSION.toString());
                }
            }
        }

        String errorMessage = String.format("Medical Record for Record ID %s does not exist", recordId);
        throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());


    }
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String deleteMedicalRecord(final Context ctx, String patientId, String recordId, String permissionPrivateKey) {
        ChaincodeStub stub = ctx.getStub();
        String medicalInfoJson = stub.getStringState(patientId);

        if (medicalInfoJson == null || medicalInfoJson.isEmpty()) {
            String errorMessage = String.format("Medical Record for Patient ID %s does not exist", patientId);
            throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
        }

        MedicalInfo medicalInfo = genson.deserialize(medicalInfoJson, MedicalInfo.class);

        for ( MedicalRecord record: medicalInfo.getMedicalRecords()) {
            if(record.getRecordId().equals(recordId)){
                if(medicalInfo.getPatientPrivateKey().equals(permissionPrivateKey) || record.getPermissions().contains(permissionPrivateKey)){

                    medicalInfo.getMedicalRecords().remove(record);

                    String deletedRecordId = recordId + "_DELETED";
                    record.setRecordId(deletedRecordId);
                    record.setComment("deleted");
                    medicalInfo.getMedicalRecords().add(record);

                    String updatedMedicalInfoJson = genson.serialize(medicalInfo);
                    stub.putStringState(patientId, updatedMedicalInfoJson);
                    return record.toString();
                }else {
                    String errorMessage = String.format("You have on permission of access to the record for Record ID %s", recordId);
                    throw new ChaincodeException(errorMessage, MedicalRecordErrors.NO_PERMISSION.toString());
                }
            }
        }

        String errorMessage = String.format("Medical Record for Record ID %s does not exist", recordId);
        throw new ChaincodeException(errorMessage, MedicalRecordErrors.RECORD_NOT_FOUND.toString());
    }
}
