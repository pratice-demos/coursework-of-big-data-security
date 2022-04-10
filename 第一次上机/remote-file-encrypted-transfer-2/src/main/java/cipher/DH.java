package cipher;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public abstract class DH {
  // DH 密钥交换算法
  private static final String KEY_ALGORITHM = "DH";
  // AES 对称加密算法
  private static final String SELECT_ALGORITHM = "AES";
  // 密钥长度
  private static final int KEY_SIZE = 512;
  // 公钥
  private static final String PUBLIC_KEY = "DHPublicKey";
  // 私钥
  private static final String PRIVATE_KEY = "DHPrivateKey";

  // 客户端初始化公私钥对
  public static Map<String, Object> initKey() throws Exception{
    // 初始化密钥对生成器
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    keyPairGenerator.initialize(KEY_SIZE);

    // 获取公私钥
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    DHPublicKey publicKey = (DHPublicKey)keyPair.getPublic();
    DHPrivateKey privateKey = (DHPrivateKey)keyPair.getPrivate();

    // 将密钥对存储在 Map 中
    Map<String, Object> keyMap = new HashMap<String, Object>(2);
    keyMap.put(PUBLIC_KEY, publicKey);
    keyMap.put(PRIVATE_KEY, privateKey);

    return keyMap;
  }

  // 服务器端初始化公私钥对
  public static Map<String, Object> initKey(byte[] key) throws Exception{
    // 利用客户端公钥初始化
    KeyFactory receiverKeyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
    PublicKey receiverPublicKey = receiverKeyFactory.generatePublic(x509EncodedKeySpec);
    DHParameterSpec dhParameterSpec = ((DHPublicKey)receiverPublicKey).getParams();

    // 初始化密钥对生成器
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    keyPairGenerator.initialize(dhParameterSpec);

    // 获取公私钥
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    DHPublicKey publicKey = (DHPublicKey)keyPair.getPublic();
    DHPrivateKey privateKey = (DHPrivateKey)keyPair.getPrivate();

    // 将密钥对存储在 Map 中
    Map<String, Object> keyMap = new HashMap<String, Object>(2);
    keyMap.put(PUBLIC_KEY, publicKey);
    keyMap.put(PRIVATE_KEY, privateKey);

    return keyMap;
  }

  // 通过一方公钥和另一方私钥构建会话密钥
  public static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) throws Exception{
    // 实例化密钥工厂
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    // 初始化公钥
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
    PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
    // 初始化私钥
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
    PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

    // AES 会话密钥构建
    KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
    keyAgree.init(priKey);
    keyAgree.doPhase(pubKey, true);
    SecretKey secretKey = keyAgree.generateSecret(SELECT_ALGORITHM);

    return secretKey.getEncoded();
  }

  // 取私钥
  public static byte[] getPrivateKey(Map<String, Object> keyMap) {
    Key key = (Key) keyMap.get(PRIVATE_KEY);
    return key.getEncoded();
  }

  // 取公钥
  public static byte[] getPublicKey(Map<String, Object> keyMap) {
    Key key = (Key) keyMap.get(PUBLIC_KEY);
    return key.getEncoded();
  }
}