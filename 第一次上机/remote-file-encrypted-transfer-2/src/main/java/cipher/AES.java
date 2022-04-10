package cipher;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

  // 生成 AES 密钥，密钥长 256b，不足用 0 填充
  private static SecretKey getKey(String password) {
    int keyLength = 256;
    byte[] keyBytes = new byte[keyLength / 8];
    SecretKeySpec key = null;
    try {
      Arrays.fill(keyBytes, (byte) 0x0);
      // 增加提供者
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      byte[] passwordBytes = password.getBytes("UTF-8");
      int length = passwordBytes.length < keyBytes.length ? passwordBytes.length : keyBytes.length;
      System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
      key = new SecretKeySpec(keyBytes, "AES");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return key;
  }

  // 初始化 AES 算法
  public static Cipher initAESCipher(String passsword, int cipherMode) {
    Cipher cipher = null;
    try {
      SecretKey key = getKey(passsword);
      cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
      cipher.init(cipherMode, key);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    return cipher;
  }

  // 加密算法
  public static Cipher encrypt(String key) {
    return initAESCipher(key, Cipher.ENCRYPT_MODE);
  }

  // 解密算法
  public static Cipher decrypt(String key) {
    return initAESCipher(key, Cipher.DECRYPT_MODE);
  }
}