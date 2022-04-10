package cypher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Utils.intToByte;
import static utils.Utils.printArrHex;

public class DHTest {
  DH dh;

  @BeforeEach
  void createDH() {
    this.dh = new DH();
  }

  @AfterEach
  void destoryDH() {
    this.dh = null;
  }

  // 测试 P、g 是否赋值正确
  @Test
  void PgTest() {
    System.out.println("P:");
    System.out.println(dh.P.toString(16));
    System.out.println("g:");
    System.out.println(dh.g.toString(16));
  }

  // 测试会话密钥生成
  @Test
  void generateSecretKeyTest() {
    byte[] priKey1 = dh.generateKeyPrivate();
    byte[] pubKey1 = dh.generateKeyPublic(priKey1);
    byte[] priKey2 = dh.generateKeyPrivate();
    byte[] pubKey2 = dh.generateKeyPublic(priKey2);
    byte[] secKey1 = dh.generateKeySecret(pubKey2, priKey1);
    byte[] secKey2 = dh.generateKeySecret(pubKey1, priKey2);
    System.out.println("priKey1:");
    printArrHex(priKey1);
    System.out.println();
    System.out.println("pubKey1:");
    printArrHex(pubKey1);
    System.out.println();
    System.out.println("priKey2:");
    printArrHex(priKey2);
    System.out.println();
    System.out.println("pubKey2:");
    printArrHex(pubKey2);
    System.out.println();
    System.out.println("secKey1:");
    printArrHex(secKey1);
    System.out.println();
    System.out.println("secKey2:");
    printArrHex(secKey2);
    assertArrayEquals(secKey1, secKey2);
  }

  // 数组添加符号
  @Test
  void addSignTest() {
    int[] key1 = {
      0x9e, 0x99, 0xe1, 0xa7,
      0x8f, 0xa2, 0xb4, 0xff,
      0x28, 0x11, 0xd9, 0x92,
      0x49, 0x31, 0x80, 0xff,
    };
    int[] key2 = {
      0x00, 0x9e, 0x99, 0xe1,
      0xa7, 0x8f, 0xa2, 0xb4,
      0xff, 0x28, 0x11, 0xd9,
      0x92, 0x49, 0x31, 0x80,
      0xff
    };
    assertArrayEquals(dh.addSign(intToByte(key1)), intToByte(key2));
  }

  // 数组删除符号
  @Test
  void removeSignTest() {
    int[] key1 = {
      0x9e, 0x99, 0xe1, 0xa7,
      0x8f, 0xa2, 0xb4, 0xff,
      0x28, 0x11, 0xd9, 0x92,
      0x49, 0x31, 0x80, 0xff,
    };
    int[] key2 = {
      0x00, 0x9e, 0x99, 0xe1,
      0xa7, 0x8f, 0xa2, 0xb4,
      0xff, 0x28, 0x11, 0xd9,
      0x92, 0x49, 0x31, 0x80,
      0xff
    };
    assertArrayEquals(intToByte(key1), dh.removeSign(intToByte(key2)));
  }
}
