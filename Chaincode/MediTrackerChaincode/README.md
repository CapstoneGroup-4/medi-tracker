# 1 前期准备

以下脚本将基于fabric及fabric-samples 2.2.0版本进行操作。

# 在部署 java 链码前保证机器上已经安装了 java, maven 环境

使用java 11版本，maven 4.0.0版本

```
java -version
```
能够正确显示java的版本信息为11。

## 1.1 下载 Chaincode 合约源码到本地机器

```
cd fabric-samples/
```
把MediTrackerChaincode代码复制到这个目录下

## 1.2 返回到test-network所在目录

返回到test-network所在目录，以便可以将链码与其他网络部件打包在一起。

```
cd test-network
```

## 1.3 将bin目录中二进制文件添加到CLI路径

所需格式的链码包可以使用peer CLI创建，使用以下命令将这些二进制文件添加到你的CLI路径。

```
export PATH=${PWD}/../bin:$PATH
```

## 1.4 设置FABRIC_CFG_PATH为指向fabric-samples中的core.yaml文件

```
export FABRIC_CFG_PATH=$PWD/../config/
```

## 1.5 启动网络

```
./network.sh up

```

## 1.6 创建通道
```
./network.sh createChannel -c mychannel
```

# 2 链码安装及部署

## 2.1 一键安装部署

### 2.2.1 修改脚本
需要修改 script/deployCC.sh 文件，将链码所在路径改为你的路径:

```
  CC_SRC_PATH=$CC_SRC_PATH/build/install/$CC_NAME
```
修改为
    
```
  CC_SRC_PATH=$CC_SRC_PATH/
```

### 2.2.2 执行部署脚本

```

./network.sh deployCC -ccn MediTracker -ccp ../MediTrackerChaincode/ -ccl java

```

## 2.2手动安装部署
### 2.2.1 创建链码包

```
peer lifecycle chaincode package MediTracker.tar.gz --path ../MediTrackerChaincode/ --lang java --label MediTracker_1.0
```

命令解释：此命令将在当前目录中创建一个名为 MediTracker.tar.gz的软件包。–lang标签用于指定链码语言，–path标签提供智能合约代码的位置，该路径必须是标准路径或相对于当前工作目录的路径，–label标签用于指定一个链码标签，该标签将在安装链码后对其进行标识。建议您的标签包含链码名称和版本。

现在，我们已经创建了链码包，我们可以在测试网络的对等节点上安装链码。

### 2.2.2 安装链码包

打包智能合约后，我们可以在peer节点上安装链码。需要在将认可交易的每个peer节点上安装链码。因为我们将设置背书策略以要求来自Org1和Org2的背书，所以我们需要在两个组织的peer节点上安装链码：peer0.org1.example.com和peer0.org2.example.com

#### Org1 peer节点安装链码

设置以下环境变量，以Org1管理员的身份操作peer CLI。

```
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_ADDRESS=localhost:7051
```

使用 peer lifecycle chaincode install 命令在peer节点上安装链码。

```
peer lifecycle chaincode install MediTracker.tar.gz
```


#### Org2 peer节点安装链码

设置以下环境变量，以Org2管理员的身份操作peer CLI。

```
export CORE_PEER_LOCALMSPID="Org2MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
export CORE_PEER_ADDRESS=localhost:9051
```

使用 peer lifecycle chaincode install 命令在peer节点上安装链码。

```
peer lifecycle chaincode install MediTracker.tar.gz
```

<strong>
<font color=red>
注意：安装链码时，链码由peer节点构建。如果智能合约代码有问题，install命令将从链码中返回所有构建错误。
因为安装 java 链码的时候需要经过 maven 构建以及下载依赖包的过程这个过程有可能会较慢，所以 install 命令有可能会返回一个超时错误:。但是其实链码的 docker 容器内此时还在执行构建任务没有完成。等到构建成功了链码包也就安装成功了。
</font>
</strong>


### 2.2.3 通过链码定义

安装链码包后，需要通过组织的链码定义。该定义包括链码管理的重要参数，例如名称，版本和链码认可策略。

如果组织已在其peer节点上安装了链码，则他们需要在其组织通过的链码定义中包括包ID。包ID用于将peer节点上安装的链码与通过的链码定义相关联，并允许组织使用链码来认可交易。

#### 查询包ID

```
peer lifecycle chaincode queryinstalled
```

包ID是链码标签和链码二进制文件的哈希值的组合。每个peer节点将生成相同的包ID。你应该看到类似于以下内容的输出：

```
Installed chaincodes on peer:
Package ID: MediTracker_1.0:df62388d799b303b9e758909ec5559605b612a433fae23fce14dd8d66fe2ad3f, Label: MediTracker_1.0
```

通过链码时，我们将使用包ID，因此，将包ID保存为环境变量。将返回的包ID粘贴到下面的命令中。
<strong>
<font color=red>
注意：包ID对于所有用户而言都不相同，因此需要使用上一步中从命令窗口返回的包ID来完成此步骤。而不是直接复制命令！！！
</font>
</strong>

```
export CC_PACKAGE_ID=MediTracker_1.0:df62388d799b303b9e758909ec5559605b612a433fae23fce14dd8d66fe2ad3f
```

#### Org2 通过链码定义

因为已经设置了环境变量为peer CLI作为Orig2管理员进行操作，所以我们可以以Org2组织级别将 hyperledger-fabric-contract-java-demo 的链码定义通过。使用 peer lifecycle chaincode approveformyorg命令通过链码定义：

```
peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name MediTracker --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem
```

#### Org1 通过链码定义

设置以下环境变量以Org1管理员身份运行：

```
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_ADDRESS=localhost:7051
```

用 peer lifecycle chaincode approveformyorg命令通过链码定义

```
peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name MediTracker --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem
```

###  2.2.4 将链码定义提交给通道

使用peer lifecycle chaincode checkcommitreadiness命令来检查通道成员是否已批准相同的链码定义：

```
peer lifecycle chaincode checkcommitreadiness --channelID mychannel --name MediTracker --version 1.0 --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --output json
```

该命令将生成一个JSON映射，该映射显示通道成员是否批准了checkcommitreadiness命令中指定的参数：

```
{
	"approvals": {
		"Org1MSP": true,
		"Org2MSP": true
	}
}
```


<strong>
<font color=red>
由于作为通道成员的两个组织都同意了相同的参数，因此链码定义已准备好提交给通道。你可以使用peer lifecycle chaincode commit命令将链码定义提交到通道。commit命令还需要由组织管理员提交。
</font>
</strong>

```
peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name MediTracker --version 1.0 --sequence 1 --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
```

可以使用peer lifecycle chaincode querycommitted命令来确认链码定义已提交给通道。

```
peer lifecycle chaincode querycommitted --channelID mychannel --name MediTracker --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem
```

如果将链码成功提交给通道，该querycommitted命令将返回链码定义的顺序和版本:
```
Version: 1.0, Sequence: 1, Endorsement Plugin: escc, Validation Plugin: vscc, Approvals: [Org1MSP: true, Org2MSP: true]
```

# 3 调用链码


```
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n MediTracker --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt -c '{"function":"initLedger","Args":[]}'

peer chaincode query -C mychannel -n MediTracker -c '{"Args":["readMedicalInfo","001"]}'
```
看到 Chaincode invoke successful. result: status:200 信息证明链码调用成功。

# 4 关闭网络

```
./network.sh down
```