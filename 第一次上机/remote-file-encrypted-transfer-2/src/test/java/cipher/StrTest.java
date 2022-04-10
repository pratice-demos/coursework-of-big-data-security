package cipher;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StrTest {
  // 测试 AES 对字符串加解密
  public void strAES(String key) throws Exception {
    // 明文
    byte[] plainText = "abcdefghijklmnopqrstuvwxyz".getBytes(StandardCharsets.UTF_8);
    System.out.println("plainText:");
    System.out.println(
      Base64.encodeBase64String(plainText)
    );
    // 加密
    Cipher enCipher = AES.encrypt(key);
    byte[] encryptText = enCipher.doFinal(plainText);
    System.out.println("encryptText:");
    System.out.println(
      Base64.encodeBase64String(encryptText)
    );
    // 解密
    Cipher deCipher = AES.decrypt(key);
    byte[] decryptText = deCipher.doFinal(encryptText);
    System.out.println("decryptText:");
    System.out.println(
      Base64.encodeBase64String(decryptText)
    );
  }

  // 测试 DH 协议生成会话密钥
  // 测试 DH 协议生成的会话密钥用于 AES 加解密
  @Test
  public void strDHTest() throws Exception {
    // 发送方生成公私钥对
    Map<String, Object> keyMap1 = DH.initKey();
    byte[] pubKey1 = DH.getPublicKey(keyMap1);
    byte[] priKey1 = DH.getPrivateKey(keyMap1);
    // 接受方生成公私钥对
    Map<String, Object> keyMap2 = DH.initKey(pubKey1);
    byte[] pubKey2 = DH.getPublicKey(keyMap2);
    byte[] priKey2 = DH.getPrivateKey(keyMap2);
    // 发送方生成会话密钥
    byte[] secKey1 = DH.getSecretKey(pubKey2, priKey1);
    System.out.println("sceKey1:");
    System.out.println(
      Base64.encodeBase64String(secKey1)
    );
    // 接受方生成会话密钥
    byte[] secKey2 = DH.getSecretKey(pubKey1, priKey2);
    System.out.println("sceKey2:");
    System.out.println(
      Base64.encodeBase64String(secKey2)
    );

    // AES 利用会话密钥加解密字符串
    strAES(Base64.encodeBase64String(secKey1));
  }
}
