# Asset.java 及 AssetTransfer.java代码说明

## 概述

**为防止配置文件可能发生错误，类名及函数名（如Asset 和 AssetTransfer）此版本暂维持不变。**

`Asset.java` 是一个表示医疗记录资产的 Java 类，定义了链上每条record中所记录的信息。

`AssetTransfer.java` 用于record的链上管理，包括增、删、改、查操作。

## Asset.java主要功能

`Asset.java`中定义了如下属性，作为一条记录计入ledger中：

**Property Definitions**:

- `private final String recordID;` // Record ID

- `private final String patientID;` // Patient ID

- `private final String hospitalID;` // Hospital ID

- `private final String doctorID;` // Doctor ID

- `private final String recordURL;` // Record URL

- `private final String recordHashCode;` // Record hash code

- `private final int version;` // Version

- `private final ZonedDateTime updateTime;` // Update time

  **其中 `recordID`作为索引，确定每条记录；`version`用来标记该条记录被修改的次数；`updateTime`由生成记录时系统自动生成**

**Code structure:**

```
javaCopy code@DataType()
public final class Asset {
    @Property()
    private final String recordID;       // Record ID

    @Property()
    private final String patientID;      // Patient ID

    @Property()
    private final String hospitalID;     // Hospital ID

    @Property()
    private final String doctorID;       // Doctor ID

    @Property()
    private final String recordURL;      // Record URL

    @Property()
    private final String recordHashCode; // Record hash code

    @Property()
    private final int version;           // Version

    @Property()
    private final ZonedDateTime updateTime; // Update time

    // Constructor
    public Asset(...) { ... }

    // Accessors
    public String getRecordID() { ... }
    public String getPatientID() { ... }
    public String getHospitalID() { ... }
    public String getDoctorID() { ... }
    public String getRecordURL() { ... }
    public String getRecordHashCode() { ... }
    public int getVersion() { ... }
    public ZonedDateTime getUpdateTime() { ... }

    // Overridden methods
    @Override
    public boolean equals(final Object obj) { ... }
    @Override
    public int hashCode() { ... }
    @Override
    public String toString() { ... }
}
```

## AssetTransfer.java主要功能

本链码包含以下主要功能：

1. **初始化账本**（`InitLedger`）
   - 该方法会在链码部署时被调用，向账本中添加一些初始的医疗记录资产，**用于测试**。
2. **创建资产**（`CreateAsset`）
   - 该方法允许创建一个新的医疗记录资产，并将其存储在账本中。
   - 在创建之前，会检查是否已经存在相同的记录 ID，如果存在，则抛出异常。
   - 若不存在既有记录，加入新纪录。
3. **读取资产**（`ReadAsset`）
   - 该方法根据提供的记录 ID 从账本中读取并返回相应的资产信息。
   - 如果资产不存在，将抛出异常。
4. **更新资产**（`UpdateAsset`）
   - **该方法允许更新现有资产的属性，并将`version`属性自动+1。**
   - **原始记录并未从ledger中删除，record ID 标记为旧版本`asset.getRecordID() + "_Version" + asset.getVersion()`**
5. **删除资产**（`DeleteAsset`）
   - 该方法允许从账本中删除指定的资产，并将其标记为已删除状态。
   - **原始记录并未从ledger中删除，record ID 标记为旧版本`asset.getRecordID() + "_Deleted"`**
6. **检查资产存在性**（`AssetExists`）
   - 该方法检查指定记录 ID 的资产是否存在于账本中。
7. **获取所有资产**（`GetAllAssets`）
   - 该方法检索账本中的所有资产，并以 JSON 格式返回。