package cypher;

import java.math.BigInteger;
import java.util.Random;

public class DH {
  // 大素数
  BigInteger P = new BigInteger("ffffffffffffffffffffffffffffff61", 16);
  // 生成元
  BigInteger g = new BigInteger("5", 16);

  // 随机生成一个私钥，128bit
  public byte[] generateKeyPrivate() {
    BigInteger priKey;
    while(true) {
      priKey = new BigInteger(128, 0, new Random());
      if(P.subtract(priKey).signum() > 0) break;
    }
    return removeSign(priKey.toByteArray());
  }

  // 生成公私钥对 128bit
  public byte[] generateKeyPublic(byte[] privateKey) {
    BigInteger priKey = new BigInteger(addSign(privateKey));
    BigInteger pubKey = g.modPow(priKey, P);
    return removeSign(pubKey.toByteArray());
  }

  // 生成会话密钥 128bit
  public byte[] generateKeySecret(byte[] publicKey, byte[] privateKey) {
    BigInteger pubKey = new BigInteger(addSign(publicKey));
    BigInteger priKey = new BigInteger(addSign(privateKey));
    BigInteger secKey = pubKey.modPow(priKey, P);
    return removeSign(secKey.toByteArray());
  }

  // 因为如果大整数首位为 1，转换为 byte 数组时会带上 0x00 正号，如果不带就会按补码解释为负数
  // 将去掉符号的 16byte 数组首部加上正号 0x00
  byte[] addSign(byte[] arr) {
    if(0x80 <= arr[0] || arr[0] <= 0xff) {
      byte[] arrr = new byte[17];
      System.arraycopy(arr, 0, arrr, 1, 16);
      return arrr;
    } else {
      return arr;
    }
  }

  // 将带有符号的 17byte 数组首部去掉符号
  byte[] removeSign(byte[] arr) {
    if(arr.length > 16) {
      byte[] arrr = new byte[16];
      System.arraycopy(arr, 1, arrr, 0, 16);
      return arrr;
    } else {
      return arr;
    }
  }
}
