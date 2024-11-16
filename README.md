# MediTracker Overall Workflow Overview

### Doctor Submits a Case Report:

Doctors submit case reports through the frontend, including patient ID, diagnostic information, report content, etc.

### Backend Processing and Data Validation:

The backend receives the request, verifies the identities and permissions of the doctor and patient.
It preprocesses the case report data and generates content to be submitted to IPFS.

### Uploading Report to IPFS:

The backend uploads the processed report content to IPFS.
IPFS returns a unique content hash value, serving as a permanent identifier for the report.

### Recording IPFS Hash on Blockchain:

The backend invokes blockchain chaincode to record information such as patient ID, report ID, and IPFS hash on the blockchain.
This confirms the data's storage and immutability via blockchain.

### Confirming Submission Result:

The backend returns the blockchain transaction results and IPFS hash to the frontend as confirmation of the report submission.

## Detailed Steps and Boundary Controls

### 1. Doctor Submits a Case Report (Frontend to Backend)

Operation: Doctors fill out a case report form in the frontend application (such as Web or mobile app), including patient ID, report content, diagnostic information, etc.
Boundary Control: The frontend performs form validation to ensure required fields are not empty. It can also authenticate the doctor's identity through OAuth or JWT to ensure the user is an authorized doctor.

### 2. Backend Receives Request and Verifies Identity (Backend)

Operation: Once the backend receives the frontend's request, it performs a secondary identity verification (such as checking JWT or Session).

Boundary Control: The backend verifies doctor's identity and permissions from the authentication and authorization system and checks if the request contains a valid patient ID. If invalid, an error message is returned.

Data Processing: Preprocess the case report, such as checking the format of the report content, filtering sensitive information, etc.

### 3. Uploading Report to IPFS (Backend and IPFS Service)

Operation: The backend uploads the case report content to IPFS using an IPFS client (such as HTTP API). Typically, this involves converting the report's textual content into JSON format or uploading the document as binary data.

Boundary Control: During the IPFS upload, ensure the data's legality (e.g., content does not contain malicious code).

IPFS Return: IPFS returns a unique content hash (CID), representing the report's storage location. This hash ensures the report's uniqueness and integrity, such that any tampering with the report changes its hash, thereby failing verification.

### 4. Storing IPFS Hash in Blockchain (Backend and Blockchain Network)

Operation: The backend invokes Hyperledger Fabric chaincode to write the following information into the blockchain:

Patient ID: Identifier associated with the patient

Report ID: Generated unique report ID (can be UUID)

IPFS Hash: Identifier for the report's storage in IPFS

Other metadata: Such as submission time, doctor ID, etc.

Permission Control: Only doctors or authorized users with specific permissions can execute this chaincode call.

Transaction Consistency: The execution of the chaincode needs to go through Hyperledger Fabric's transaction process, including endorsement and ordering, to ensure consistency of data across multiple nodes.

Chaincode Logic: The chaincode contains record verification logic, such as checking the uniqueness of patient ID and report ID, ensuring the integrity of data on the chain.

### 5. Confirm Submission Result and Return to Frontend (Backend to Frontend)

Operation: Once the backend receives the successful transaction information from the blockchain, it returns the IPFS hash and confirmation status to the frontend.

Boundary Control: After confirming the transaction's success, the backend sends the returned hash and other confirmation information to the frontend.

Frontend Display: Upon receiving the results, the frontend can show the doctor that the report has been successfully submitted, including the IPFS hash and the submission timestamp, serving as an archival proof.

## Detailed Boundary Controls Explanation

Throughout the entire process, the following are the main boundary control measures:

### Identity Authentication and Permission Verification:

The frontend authenticates the doctor's identity (e.g., OAuth, JWT) when submitting the report.
The backend performs a secondary verification to ensure the authenticity of the doctor's identity and the validity of permissions.

### Data Integrity and Legality Check:

Both the frontend and backend perform checks on the data's legality and integrity to prevent illegal data submission.
Before IPFS upload, the data content is verified to ensure it meets format specifications.

### Reliability of IPFS and Blockchain Interactions:

The generation and storage of IPFS hashes ensure the data's uniqueness and immutability.
Using blockchain to write IPFS hash and metadata into the chaincode ensures report persistence and security with patient ID and other information.
Transaction Traceability and Immutability:

In Hyperledger Fabric, all chaincode transactions are traceable, and once a transaction is successfully committed, the data on the chain cannot be changed. This ensures a trustworthy historical record of the report submission process.

## Key Technology Choices

Frontend Authentication: OAuth, JWT, and other identity authentication mechanisms ensure the legality of the doctor's identity.

Backend and IPFS Interaction: Use an IPFS client or HTTP API to upload case reports to IPFS and obtain a unique content hash.

Hyperledger Fabric Chaincode: Store patient ID, report ID, IPFS hash, and metadata in the chaincode to ensure data persistence and security.

Data Query and Access Control: Provide read-only interfaces through chaincode to allow the frontend to access case report information. When the frontend needs to query data, it can call the chaincode to retrieve the report's hash and metadata.


# MediTracker整体流程概览
### 医生提交病例报告：

医生通过前端提交病例报告，包含患者ID、诊断信息、报告内容等。

### 后端处理和数据验证：

后端接收请求，验证医生和患者的身份和权限。
对病例报告数据进行预处理，并生成提交给IPFS的内容。

### 上传报告到IPFS：

后端将处理后的报告内容上传到IPFS。
IPFS返回唯一的内容哈希值，作为该报告的永久标识符。

### 将IPFS哈希记录到区块链：

后端调用区块链的链码，将患者ID、报告ID和IPFS哈希值等信息写入区块链。
通过区块链确认数据的存储和不可篡改性。

### 确认提交结果：

后端将区块链的交易结果和IPFS哈希返回给前端，作为报告提交的确认信息。

## 步骤详解及边界控制

### 1. 医生提交病例报告（前端到后端）
操作：医生在前端应用（如Web或移动应用）中填写病例报告表单，包括患者ID、报告内容、诊断信息等。
边界控制：前端需要先进行表单验证，确保必填字段不为空。前端还可以通过OAuth或JWT进行医生身份验证，以确保用户是经过授权的医生。

### 2. 后端接收请求并验证身份（后端）
操作：后端接收到前端的请求后，对请求中的医生身份进行二次验证（如检查JWT或Session）。

边界控制：后端从认证和权限系统中验证医生身份和权限，并检查请求中是否包含有效的患者ID。若无效，则返回错误信息。

数据处理：对病例报告进行预处理，如检查报告内容的格式，过滤敏感信息等。

### 3. 上传报告到IPFS（后端与IPFS服务）
操作：后端通过IPFS客户端（如HTTP API）将病例报告的内容上传到IPFS。通常是将报告的文字内容转为JSON格式，或将文档文件以二进制数据上传。

边界控制：IPFS上传过程中，确保数据的合法性（如内容不包含恶意代码等）。

IPFS返回：IPFS返回一个唯一的内容哈希值（CID），代表报告的存储位置。这个哈希值确保了报告的唯一性和完整性，即使报告被篡改，其哈希值会发生变化，从而无法被验证。

### 4. 将IPFS哈希存入区块链（后端与区块链网络）
操作：后端调用Hyperledger Fabric链码，将以下信息写入区块链：

患者ID：报告关联的患者标识符

报告ID：生成的唯一报告ID（可以是UUID）

IPFS哈希：报告在IPFS中的存储标识

其他元数据：如提交时间、医生ID等

权限控制：只有具备特定权限的医生或授权用户才能执行此链码调用。

事务一致性：链码的执行需要通过Hyperledger Fabric的交易流程，包括背书和排序，确保数据在多个节点上的一致性。

链码逻辑：链码内包含记录验证逻辑，例如检查患者ID和报告ID的唯一性，确保链上数据的完整性。

### 5. 确认提交结果并返回前端（后端到前端）
操作：后端收到区块链的交易成功信息后，将IPFS哈希值和确认状态返回给前端。

边界控制：在后端确认交易成功后，将返回的哈希值和其他确认信息给前端。

前端展示：前端接收到结果后，可以向医生展示报告已成功提交的信息，包括报告的IPFS哈希值和提交的时间戳等，作为存档凭证。

## 边界控制详解

在整个流程中，以下是主要的边界控制措施：

### 身份认证和权限验证：

前端在医生提交报告时进行身份验证（如OAuth、JWT）。
后端进行二次验证，以确保医生身份的真实性和权限的有效性。

### 数据完整性和合法性检查：

前端和后端均进行数据的合法性和完整性检查，以防止非法数据提交。
IPFS上传前对数据内容进行验证，确保上传内容符合格式规范。

### IPFS和区块链交互的可靠性：

IPFS哈希的生成和存储确保了数据的唯一性和不可篡改性。
使用区块链将IPFS哈希值和元数据写入链码中，确保报告与患者ID等信息的持久化和不可篡改性。
交易的可追溯性和不可篡改性：

在Hyperledger Fabric中，所有链码交易都是可追溯的，一旦交易提交成功，链上数据不可更改。这确保了报告的提交过程具有可信赖的历史记录。

## 关键技术选择
前端认证：OAuth、JWT等身份认证机制，确保医生身份的合法性。

后端与IPFS交互：通过IPFS客户端或HTTP API将病例报告上传到IPFS，获取唯一的内容哈希。

Hyperledger Fabric链码：在链码中存储患者ID、报告ID、IPFS哈希和元数据，确保数据持久化和安全性。

数据查询与访问控制：通过链码提供只读接口，允许前端访问病例报告信息，前端需要查询数据时可以调用链码来获取报告的哈希和元数据。
