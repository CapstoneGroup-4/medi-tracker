# Chaincode 说明

### 更新说明 - 20241117

1. 将代码中ArrayList修改为Set集合，达到自动去重目的；
2. MedicalRecord新增private final String ipfsFileId，用于存放MedicalRecord存放地址
3. doctor改为doctorID，使意义更清晰；
4. 测试网测试chaincode。

### 更新说明 - 20241110

1. 修改chaincode调用结果无内容显示bug
2. 在 medical record 中增加 recordVersion，comment，permissions 字段，实现增删改查版本控制及权限控制功能。
3. 在medical info 中增加 medicalVersion，comment，patientPrivateKey 字段，实现增删改查版本控制及权限控制功能。
4. 采用arraylist代替原数组数据格式，增加数据增删改灵活性。
5. 完善medical record 及 medical info 增删改查功能。
6. 实现医疗数据权限控制功能。
7. 测试网测试chaincode。

------

### 1. **MedicalRecord类（医疗记录）**

- 属性：
  - `recordId`：记录ID，唯一标识一个医疗记录。
  - `date`：医疗记录的日期。
  - `doctorID`：负责该医疗记录的医生ID。
  - `clinic`：提供医疗服务的诊所名称。
  - `detail`：记录的详细描述。
  - `recordVersion`：记录的版本号，用于跟踪更新（新增）。
  - `comment`：关于该记录的备注（新增）。
  - `ipfsField`：用于存放病历地址。（新增）
  - `permissions`：允许查看或修改该记录的权限列表（新增）。
- 方法：
  - 提供了获取和设置每个属性的getter和setter方法。
  - 实现了`equals()`、`hashCode()`和`toString()`方法，用于比较和输出对象信息。注意，版本号、备注和权限不会被用于比较操作。

------

### 2. **MedicalInfo 类（病人医疗信息）**

- 属性：
  1. **`patientId`**：病人ID，唯一标识一个病人。
  2. **`patientName`**：病人姓名。
  3. **`patientGender`**：病人性别。
  4. **`patientEmail`**：病人的电子邮件地址。
  5. **`medicalVersion`**：病人的医疗信息版本号。
  6. **`comment`**：关于医疗信息的备注。
  7. **`patientPrivateKey`**：病人的私钥。
  8. **`medicalRecords`**：病人的医疗记录列表（`Set`）。
- 方法：
  - 提供了获取和设置每个属性的getter和setter方法。
  - 实现了`equals()`、`hashCode()`和`toString()`方法，用于比较和输出对象信息。

---

### 3. **MedicalRecordContract类（医疗记录智能合约）**

- **数据序列化工具：**
    - 使用 `Genson` 进行 JSON 数据的序列化和反序列化，用于将医疗信息对象存储到区块链上。
- **自定义错误类型（MedicalRecordErrors枚举）：**
    - `RECORD_NOT_FOUND`：找不到指定的记录。
    - `RECORD_ALREADY_EXISTS`：记录已经存在，无法重复创建。
    - `NO_PERMISSION`：用户没有访问或修改记录的权限。

---

### 功能方法说明

1. **initLedger方法（初始化账本）**
    - 作用：初始化账本，添加一些测试数据，包括病人的医疗信息和相应的医疗记录。
    - 每个病人的信息都包含病人的姓名、性别、电子邮件和一系列的医疗记录，数据通过 `stub.putStringState` 存储到区块链中。
2. **readMedicalInfo方法（读取病人医疗信息）**
    - 作用：根据病人的ID和私钥读取医疗信息，确保只有病人自己能查看所有的医疗记录。
    - 逻辑：先检查病人ID对应的医疗信息是否存在，然后根据病人的私钥进行权限验证。
3. **createMedicalInfo方法（创建病人医疗信息）**
    - 作用：创建新的病人医疗信息，并将其存储在区块链上。
    - 逻辑：在创建时，会检查该病人的信息是否已经存在，避免重复创建。
4. **updateMedicalInfo方法（更新病人医疗信息）**
    - 作用：更新病人的医疗信息。只有病人本人可以更新自己的信息。
    - 逻辑：首先验证私钥权限，确保只有病人可以进行更新操作。更新时会递增版本号，并标注为“updated”。
5. **deleteMedicalInfo方法（删除病人医疗信息）**
    - 作用：从区块链中删除指定的病人信息，删除操作后会保留一个带有 "_DELETED" 后缀的记录副本，确保历史记录可追溯。
    - 逻辑：验证权限后，将记录标记为删除，并更新区块链上的状态。
6. **medicalRecordExists方法（检查医疗记录是否存在）**
    - 作用：检查指定病人的医疗信息是否存在于区块链中。
    - 逻辑：通过 `stub.getStringState` 获取病人信息，判断是否为空。
7. **addMedicalRecord方法（添加新的医疗记录）**
    - 作用：向指定的病人信息中添加新的医疗记录。只有病人自己或有权限的医生才能添加记录。
    - 逻辑：检查记录是否已存在，验证权限，添加后将更新后的病人信息存储在区块链中。
8. **readMedicalRec方法（读取单个医疗记录）**
    - 作用：读取病人的某条特定医疗记录。只有病人或被授权的人员可以访问该记录。
    - 逻辑：验证权限，如果记录存在且用户有权限查看，则返回该记录的详细信息。
9. **updateMedicalRecord方法（更新医疗记录）**
    - 作用：更新病人的某条医疗记录。版本号会递增，备注会标记为“updated”。
    - 逻辑：删除旧记录，增加新记录，同时确保权限检查。
10. **deleteMedicalRecord方法（删除医疗记录）**
    - 作用：删除指定的医疗记录，删除后会保留一个带有 "_DELETED" 后缀的记录副本。
    - 逻辑：在权限验证通过后，执行删除操作，并将被删除的记录做标记以确保历史可追溯。

---

### 版本控制

为保证账本的不可更改及可追溯性，所有针对medical record 及 medical info 的 update 和 delete操作并未对原记录进行覆盖，而是保留原版本记录的同时，建立新记录。

如：创建一条新medical info，相关信息如下：

1. **`patientId` = “001”**
2. **`patientName` = “Tom”**
3. **`medicalVersion` = 0**
4. **`comment` = “original”**

若对该记录进行delete操作，本记录并未从ledger中删去，而是自动将相关信息如下修改，并继续保存在ledger中：

1. **`patientId` = “001_DELETED”**
2. **`patientName` = “Tom”**
3. **`medicalVersion` = 0**
4. **`comment` = “original”**

若对该记录进行update操作，则对原纪录进行delete操作，而update后的信息自动 **`medicalVersion + 1` ，`comment` = “updated”：**

1. **`patientId` = “001”**
2. **`patientName` = “Tom”**
3. **`medicalVersion` = 1**
4. **`comment` = “update”**

若用户想回溯已删除及更新前的信息，只需查询对应ID及版本号记录即可。

---

### 权限控制

对于medical info 及全部medical record 信息的访问及修改，Patient具有完全权限，每次调用链码时，链码会自动核实传入的patientPrivateKey 或 permissionPrivateKey 是否具有权限，即执行如下语句判断：

```java
if(medicalInfo.getPatientPrivateKey().equals(patientPrivateKey)){
            ;
        }

```

对于medical record，除patient本人具有权限外，本record的上传医生也具有相同权限；但医生对于非自身上传的medical record，无任何权限。

```java
if(medicalInfo.getPatientPrivateKey().equals(permissionPrivateKey) || record.getPermissions().contains(permissionPrivateKey)){
		;
}

```

---

### Test network 测试代码

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