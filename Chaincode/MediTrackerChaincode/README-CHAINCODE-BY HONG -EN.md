# CHAINCODE

### Update Notes - 20241117

1. Changed ArrayList to Set in the code to automatically remove duplicates.
2. Added `private final String ipfsFileId` to the `MedicalRecord` class to store the address of the medical record.
3. Changed `doctor` to `doctorID` to clarify its meaning.
4. Tested the chaincode on the test network.

### Update Notes - 20241110

1. Fixed the bug where chaincode invocation results were not displayed.
2. Added `recordVersion`, `comment`, and `permissions` fields to the `medicalRecord` to implement version control and access control for create, read, update, and delete (CRUD) operations.
3. Added `medicalVersion`, `comment`, and `patientPrivateKey` fields to the `medicalInfo` to implement version control and access control for CRUD operations.
4. Replaced the original array data structure with an `ArrayList` to increase flexibility in data modification.
5. Improved the CRUD functionality for `medicalRecord` and `medicalInfo`.
6. Implemented access control for medical data.
7. Tested the chaincode on the test network.

------

### 1. **MedicalRecord Class (Medical Record)**

- Attributes:
  - `recordId`: The record ID, which uniquely identifies a medical record.
  - `date`: The date of the medical record.
  - `doctorID`: The ID of the doctor responsible for the medical record.
  - `clinic`: The name of the clinic providing medical services.
  - `detail`: A detailed description of the record.
  - `recordVersion`: The version number of the record for tracking updates (newly added).
  - `comment`: Remarks related to the record (newly added).
  - `ipfsField`: The field used to store the address of the medical record (newly added).
  - `permissions`: A list of permissions allowing access or modification of the record (newly added).
- Methods:
  - Provides getter and setter methods for each attribute.
  - Implements `equals()`, `hashCode()`, and `toString()` methods for object comparison and output. Note that version number, remarks, and permissions are not used in comparison operations.

------

### 2. **MedicalInfo Class (Patient Medical Information)**

- Attributes:
  1. **`patientId`**: The patient ID, which uniquely identifies a patient.
  2. **`patientName`**: The patient's name.
  3. **`patientGender`**: The patient's gender.
  4. **`patientEmail`**: The patient's email address.
  5. **`medicalVersion`**: The version number of the patient's medical information.
  6. **`comment`**: Remarks related to the medical information.
  7. **`patientPrivateKey`**: The patient's private key.
  8. **`medicalRecords`**: A list of the patient's medical records (`Set`).
- Methods:
  - Provides getter and setter methods for each attribute.
  - Implements `equals()`, `hashCode()`, and `toString()` methods for object comparison and output.

---

### 3. **MedicalRecordContract Class (Smart Contract for Medical Records)**

- **Data Serialization Tool:**
    - `Genson` is used for JSON data serialization and deserialization to store medical information objects on the blockchain.
- **Custom Error Types (MedicalRecordErrors Enum):**
    - `RECORD_NOT_FOUND`: Indicates that the specified record was not found.
    - `RECORD_ALREADY_EXISTS`: Indicates that the record already exists and cannot be created again.
    - `NO_PERMISSION`: Indicates that the user does not have permission to access or modify the record.

---

### Functional Methods Description

1. **`initLedger` Method (Initialize the Ledger)**
    - **Purpose**: Initializes the ledger by adding some test data, including patients' medical information and corresponding medical records.
    - **Logic**: Each patient's information includes their name, gender, email, and a series of medical records. Data is stored on the blockchain via `stub.putStringState`.
2. **`readMedicalInfo` Method (Read Patient's Medical Information)**
    - **Purpose**: Reads the patient's medical information based on their ID and private key, ensuring that only the patient can view all their medical records.
    - **Logic**: It first checks if the medical information for the patient ID exists and then verifies access based on the patient's private key.
3. **`createMedicalInfo` Method (Create Patient’s Medical Information)**
    - **Purpose**: Creates new patient medical information and stores it on the blockchain.
    - **Logic**: It checks whether the patient information already exists to avoid duplication before creating a new record.
4. **`updateMedicalInfo` Method (Update Patient’s Medical Information)**
    - **Purpose**: Updates the patient's medical information. Only the patient can update their own information.
    - **Logic**: It first verifies access via the patient's private key to ensure only the patient can perform the update. The version number is incremented and marked as "updated" during the process.
5. **`deleteMedicalInfo` Method (Delete Patient’s Medical Information)**
    - **Purpose**: Deletes the specified patient's information from the blockchain. After deletion, a copy of the record with a "_DELETED" suffix is retained to ensure traceability.
    - **Logic**: After permission verification, the record is marked as deleted, and the blockchain state is updated.
6. **`medicalRecordExists` Method (Check if Medical Record Exists)**
    - **Purpose**: Checks if a patient's medical information exists on the blockchain.
    - **Logic**: It retrieves patient information using `stub.getStringState` and checks if it is null.
7. **`addMedicalRecord` Method (Add a New Medical Record)**
    - **Purpose**: Adds a new medical record to the specified patient's information. Only the patient or an authorized doctor can add a record.
    - **Logic**: It checks if the record already exists, verifies permissions, and updates the patient information on the blockchain after adding the record.
8. **`readMedicalRec` Method (Read a Single Medical Record)**
    - **Purpose**: Reads a specific medical record for a patient. Only the patient or an authorized individual can access this record.
    - **Logic**: It verifies permissions. If the record exists and the user has access, it returns the record's details.
9. **`updateMedicalRecord` Method (Update a Medical Record)**
    - **Purpose**: Updates a specific medical record for a patient. The version number is incremented, and comments are marked as "updated."
    - **Logic**: The old record is deleted, and a new record is created after permission verification.
10. **`deleteMedicalRecord` Method (Delete a Medical Record)**
    - **Purpose**: Deletes the specified medical record. A copy of the record with a "_DELETED" suffix is retained after deletion.
    - **Logic**: After permission verification, the record is deleted, and a marked copy is saved for historical traceability.

---

### Version Control

To ensure the immutability and traceability of the ledger, all **update** and **delete** operations for medical records and medical information do not overwrite the original records. Instead, new records are created while the original versions are retained.

Example: When creating a new **MedicalInfo**, the data might look like this:

1. **`patientId` = “001”**
2. **`patientName` = “Tom”**
3. **`medicalVersion` = 0**
4. **`comment` = “original”**

If a **delete** operation is performed on this record, the record is not removed from the ledger. Instead, the following information is automatically modified and stored in the ledger:

1. **`patientId` = “001_DELETED”**
2. **`patientName` = “Tom”**
3. **`medicalVersion` = 0**
4. **`comment` = “original”**

If an **update** operation is performed on this record, the original record is marked as deleted, and the updated information is saved with the following changes: **`medicalVersion + 1`** and **`comment = “updated”`**:

1. **`patientId` = “001”**
2. **`patientName` = “Tom”**
3. **`medicalVersion` = 1**
4. **`comment` = “updated”**

If the user wants to trace deleted or updated records, they can query the corresponding ID and version number.

---

### Permission Control

For accessing and modifying **MedicalInfo** and all associated **MedicalRecord** information, the **patient** has full control. Every time a chaincode call is made, the system verifies the patient's `patientPrivateKey` or `permissionPrivateKey` to ensure permissions by checking the following:

```java
if(medicalInfo.getPatientPrivateKey().equals(patientPrivateKey)){
    // Access granted
}

```

For **MedicalRecord** access, in addition to the patient, the doctor who uploaded the record also has permission. However, doctors cannot access records they did not upload.

```java
if(medicalInfo.getPatientPrivateKey().equals(permissionPrivateKey) || record.getPermissions().contains(permissionPrivateKey)){
    // Access granted
}

```

---

### Test Network Code Examples

```json
// initLeger()
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n MediTracker --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt -c '{"function":"initLedger","Args":[]}'

// readMedicalInfo()
peer chaincode query -C mychannel -n MediTracker -c '{"Args":["readMedicalInfo","002", "patient002__PrivateKey"]}'

peer chaincode query -C mychannel -n MediTracker -c '{"Args":["readMedicalInfo","001", "Dr.Smith_PrivateKey"]}'

peer chaincode query -C mychannel -n MediTracker -c '{"Args":["readMedicalInfo","003", "Dr.Smith_PrivateKey"]}'

// readMedicalRec()
peer chaincode query -C mychannel -n MediTracker -c '{"Args":["readMedicalRec","001","record1", "Dr.Smith_PrivateKey"]}'

peer chaincode query -C mychannel -n MediTracker -c '{"Args":["readMedicalRec","001","record1", "Dr.Jones_PrivateKey"]}'

peer chaincode query -C mychannel -n MediTracker -c '{"Args":["readMedicalRec","001","record3", "Dr.Jones_PrivateKey"]}'

//deleteMedicalRecord()
peer chaincode query -C mychannel -n MediTracker -c '{"Args":["deleteMedicalRecord","001","record3", "Dr.Jones_PrivateKey"]}'

peer chaincode query -C mychannel -n MediTracker -c '{"Args":["deleteMedicalRecord","001","record1", "Dr.Jones_PrivateKey"]}'

peer chaincode query -C mychannel -n MediTracker -c '{"Args":["deleteMedicalRecord","001","record2", "Dr.Jones_PrivateKey"]}'

```