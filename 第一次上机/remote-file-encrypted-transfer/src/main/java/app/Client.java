package app;

import cypher.AES;
import cypher.DH;

import java.io.*;
import java.net.Socket;

import static utils.Utils.*;

public class Client {
  // 会话
  Socket socket;
  // 公私钥以及会话密钥
  private byte[] priKey1;
  private byte[] pubKey1;
  private byte[] pubKey2;
  private byte[] secKey1;
  DH dh = new DH();
  // 加密
  AES aes = new AES();

  // 连接服务器
  void connect(String IP, int PORT) {
    try {
      socket = new Socket(IP, PORT);
      // 发起 DH 密钥交换
      initSecKey();
    } catch (Exception e) {
      System.out.println("连接失败");
      e.printStackTrace();
    }
  }

  // 根据 DH 协议生成会话密钥
  void initSecKey() {
    try {
      DataInputStream dis = new DataInputStream(socket.getInputStream());
      DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      // 生成私钥和公钥
      priKey1 = dh.generateKeyPrivate();
      pubKey1 = dh.generateKeyPublic(priKey1);
      System.out.println("客户端私钥：");
      printArrHex(priKey1);
      System.out.println();
      System.out.println("客户端公钥：");
      printArrHex(pubKey1);
      System.out.println();

      // 发送客户端公钥
      dos.write(pubKey1);

      // 接受服务端公钥
      pubKey2 = new byte[16];
      dis.readFully(pubKey2);
      System.out.println("服务端公钥：");
      printArrHex(pubKey2);
      System.out.println();

      // 生成会话密钥
      secKey1 = dh.generateKeySecret(pubKey2, priKey1);
      System.out.println("会话密钥：");
      printArrHex(secKey1);
      System.out.println();

      // 初始化 AES 加密
      aes.initKey(secKey1);

      System.out.println("连接成功");
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 发送文件
  void sendFile(String filePath, String fileName) {
    try {
      DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      DataInputStream dis = new DataInputStream(socket.getInputStream());
      // 说明发送文件
      dos.writeUTF("send file");
      String command;
      command = dis.readUTF();
      if(command.equals("no")) {
        throw new Exception("服务失败");
      }
      // 判断文件是否存在
      File file = new File(filePath + File.separator + fileName);
      if(!file.exists()) {
        throw new FileNotFoundException(fileName);
      }
      // 发送文件名
      dos.writeUTF(fileName);
      // 发送文件
      byte[] buffer = new byte[1024];
      byte[] enBuffer;
      int length;
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      while ((length = bis.read(buffer)) != -1) {
        enBuffer = aes.encryptText(buffer);
        // 发送数据大小和数据内容
        dos.writeInt(length);
        dos.write(enBuffer);
      }
      // 发送完毕
      dos.writeInt(0);
      // 发送成功
      if(dis.readUTF().equals("ok")) {
        System.out.println("发送成功");
      } else {
        throw new Exception("发送失败");
      }
      bis.close();
      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Client client = new Client();
    client.connect("127.0.0.1", 8888);
    client.sendFile("src/main/resources/send", "default-cover.jpg");
//    client.sendFile("src/main/resources/send", "demo.txt");
  }
}
