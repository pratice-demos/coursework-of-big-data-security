### 介绍
第一次上机作业，实现加密文件远程传输

#### 技术选用
java & maven

#### 说明
AES、DH 交换都是调用 java 提供库实现

另外，AES 256bit，DH 公私钥 512bit，所以在 jdk8 以后运行可能会有报错，需要在 JVM options 添加 ```-Djdk.crypto.KeyAgreement.legacyKDF=true```