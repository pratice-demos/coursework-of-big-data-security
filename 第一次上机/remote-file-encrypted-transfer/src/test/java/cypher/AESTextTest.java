package cypher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static utils.Utils.intToByte;
import static utils.Utils.printArrHex;

public class AESTextTest {
  AES aes;

  @BeforeEach
  void createAES() {
    int[] key = {
      0x0f, 0x15, 0x71, 0xc9,
      0x47, 0xd9, 0xe8, 0x59,
      0x0c, 0xb7, 0xad,
    };
    this.aes = new AES();
    this.aes.initKey(intToByte(key));
  }

  @AfterEach
  void destoryAES() {
    this.aes = null;
  }

  // 测试密钥扩展，测试第二轮密钥是否正确
  @Test
  void keyExpansionTest() {
    printArrHex(this.aes.keyList);
  }

  // 测试 AES 加密和解密
  @Test
  void enAndDeTest() {
    int[] str = {
      0x01, 0x23, 0x45, 0x67,
      0x89, 0xab, 0xcd, 0xef,
      0xfe, 0xdc, 0xba, 0x98,
      0x76, 0x54, 0x32, 0x10,
      0x89, 0xab, 0xcd, 0xef,
      0xfe, 0xdc, 0xba, 0x98,
      0x76, 0x54, 0x32, 0x10,
      0xfe, 0xdc, 0xba, 0x98,
      0x76, 0x54, 0x32, 0x10,
      0x89, 0xab, 0xcd, 0xef,
      0xfe, 0xdc, 0x00, 0x00,
      0x00, 0x00, 0x00, 0x00,
    };
    System.out.println("plainText:");
    printArrHex(intToByte(str));
    System.out.println();
    byte[] str1 = this.aes.encryptText(intToByte(str));
    System.out.println("encryptText:");
    printArrHex(str1);
    System.out.println();
    byte[] str2 = this.aes.decryptText(str1);
    System.out.println("decryptText:");
    printArrHex(str2);
    assertArrayEquals(intToByte(str), str2);
  }
}
