package upload;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import cipher.AES;
import cipher.DH;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

public class TCPClient {
  // 客户端公私钥
  private static byte[] publicKey2;
  private static byte[] privateKey2;
  // 服务端公钥
  private static byte[] publicKey1;
  // 会话密钥
  private static byte[] key2;

  // 初始化公私钥对
  private void initKeyPair() {
    try {
      Map<String, Object> keyMap2 = DH.initKey();
      publicKey2 = DH.getPublicKey(keyMap2);
      privateKey2 = DH.getPrivateKey(keyMap2);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("客户端公钥:\n" + Base64.encodeBase64String(publicKey2));
    System.out.println("客户端私钥:\n" + Base64.encodeBase64String(privateKey2));
  }

  // 生成服务端密钥对
  private void initSessionKey() {
    try {
      key2 = DH.getSecretKey(publicKey1, privateKey2);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("会话密钥:\n" + Base64.encodeBase64String(key2));
  }

  // 初始化
  private void init(String IP, int PORT, String sendFilePath, String sendFileName) throws IOException {
    // 初始化公私钥对
    initKeyPair();

    Socket socket = new Socket(IP, PORT);
    OutputStream os = socket.getOutputStream();
    InputStream is = socket.getInputStream();

    // DH 密钥交换
    // 将客户端公钥发给服务端
    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    dos.writeInt(publicKey2.length);
    dos.write(publicKey2);

    // 接收服务器端的公钥
    DataInputStream dis = new DataInputStream(socket.getInputStream());
    publicKey1 = new byte[dis.readInt()];
    dis.readFully(publicKey1);
    System.out.println("服务端公钥:\n" + Base64.encodeBase64String(publicKey1));

    // 生成会话密钥
    initSessionKey();

    // 获取 AES 加密对象
    Cipher cipher = AES.encrypt(Base64.encodeBase64String(key2));

    // 发送文件
    // 发送文件名
    dos.writeUTF(sendFileName);
    System.out.println("发送文件名:" + sendFileName);

    // 判断文件是否存在
    File file = new File(sendFilePath + File.separator + sendFileName);
    if(!file.exists()) {
      file.createNewFile();
    }

    // 发送加密文件
    CipherInputStream cis = new CipherInputStream(new FileInputStream(file), cipher);
    int len = 0;
    byte[] bytes = new byte[1024];
    while((len = cis.read(bytes)) != -1) {
      os.write(bytes, 0, len);
    }

    // 告诉服务器已发送完毕
    socket.shutdownOutput();
    // 读取服务器返回的数据
    while((len = is.read(bytes)) != -1) {
      System.out.println(new String(bytes, 0, len));
    }

    // 释放资源
    cis.close();
    socket.close();
  }

  // 创建客户端程序
  public TCPClient(String IP, int PORT, String sendFilePath, String sendFileName) throws IOException {
    init(IP, PORT, sendFilePath, sendFileName);
  }

  public static void main(String[] args) throws IOException {
    TCPClient client = new TCPClient(
      "127.0.0.1", 8888,
      "src/main/resources/sendFile",
      "default-cover.jpg"
    );
  }
}