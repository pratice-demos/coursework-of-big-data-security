package utils;

public class Utils {
  // 将 16 数组按列填充转换为 4*4 数组
  public static byte[][] convertToArrayReverse(byte[] str) {
    byte[][] text = new byte[4][4];
    for(int i=0; i<4; i++) {
      for(int j=0; j<4; j++) {
        text[i][j] = str[4*j + i];
      }
    }
    return text;
  }

  // 将 16 数组按行填充转换为 4*4 数组
  public static byte[][] convertToArray(byte[] str) {
    byte[][] text = new byte[4][4];
    for(int i=0; i<4; i++) {
      for(int j=0; j<4; j++) {
        text[i][j] = str[4*i + j];
      }
    }
    return text;
  }

  // 将 4*4 数组按列填充转换为 16 数组
  public static byte[] convertToStringReverse(byte[][] text) {
    byte[] str = new byte[16];
    for(int i=0; i<4; i++) {
      for(int j=0; j<4; j++) {
        str[4*j + i] = text[i][j];
      }
    }
    return str;
  }

  // 将多维数组以 16 进制形式打印输出
  public static void printArrHex(byte[] text) {
    for(int i=0; i<text.length; i++) {
      System.out.printf("0x%02x, ", text[i]);
      if((i+1)%4 == 0) {
        System.out.println();
      }
    }
  }

  public static void printArrHex(byte[][] text) {
    for(int i=0; i<text.length; i++) {
      for(int j=0; j<text[i].length; j++) {
        System.out.printf("0x%02x, ", text[i][j]);
      }
      System.out.println();
    }
  }

  // 将 int 数组转换为 byte 数组
  public static byte[] intToByte(int[] arr) {
    byte[] arrr = new byte[arr.length];
    for(int i=0; i<arr.length; i++) {
      arrr[i] = (byte) arr[i];
    }
    return arrr;
  }

  public static byte[][] intToByte(int[][] arr) {
    byte[][] arrr = new byte[arr.length][arr[0].length];
    for(int i=0; i<arr.length; i++) {
      for(int j=0; j<arr[i].length; j++) {
        arrr[i][j] = (byte) arr[i][j];
      }
    }
    return arrr;
  }
}
