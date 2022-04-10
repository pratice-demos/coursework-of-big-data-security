package app;

import cypher.AES;
import cypher.DH;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import static utils.Utils.printArrHex;

public class Server {
  // 当前会话
  Socket socket;
  // 公私钥及会话密钥
  byte[] priKey2;
  byte[] pubKey2;
  byte[] pubKey1;
  byte[] secKey2;
  DH dh = new DH();
  // 加密
  AES aes = new AES();

  // 启动服务器
  void start(int PORT) {
    try {
      ServerSocket server = new ServerSocket(PORT);
      while (true) {
        // 接受连接
        socket = server.accept();
        System.out.println(
          "来访客户信息:\n" +
          "客户端IP：" + socket.getInetAddress() + "\n" +
          "客户端端口：" + socket.getInetAddress().getLocalHost() + "\n" +
          "已连接服务器\n"
        );
        // 发起 DH 密钥交换
        initSecKey();
        // 接受文件
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        // 判断是否发送文件
        String command;
        command = dis.readUTF();
        if(command.equals("send file")) {
          dos.writeUTF("ok");
          acceptFile("src/main/resources/receive", "src/main/resources/encrypt");
        } else {
          dos.writeUTF("no");
          System.out.println("服务失败");
          System.out.println();
        }
      }
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
      priKey2 = dh.generateKeyPrivate();
      pubKey2 = dh.generateKeyPublic(priKey2);
      System.out.println("服务端私钥：");
      printArrHex(priKey2);
      System.out.println();
      System.out.println("服务端公钥：");
      printArrHex(pubKey2);
      System.out.println();

      // 接受客户端公钥
      pubKey1 = new byte[16];
      dis.readFully(pubKey1);
      System.out.println("客户端公钥：");
      printArrHex(pubKey1);
      System.out.println();

      // 生成会话密钥
      secKey2 = dh.generateKeySecret(pubKey1, priKey2);
      System.out.println("会话密钥：");
      printArrHex(secKey2);
      System.out.println();

      // 发送服务端公钥
      dos.write(pubKey2);

      // 初始化 AES 加密
      aes.initKey(secKey2);
      System.out.println("连接成功");
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void acceptFile(String filePath, String tempPath) {
    try {
      DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      DataInputStream dis = new DataInputStream(socket.getInputStream());
      // 接受文件名
      String fileName = dis.readUTF();
      Date date = new Date();
      fileName = date.getTime() + fileName;
      System.out.println("接受文件：");
      System.out.println(fileName);
      // 接受文件
      byte[] buffer = new byte[1024];
      byte[] deBuffer;
      int length;
      File receiveFile = new File(filePath + File.separator + fileName);
      File tempFile = new File(tempPath + File.separator + fileName);
      BufferedOutputStream receiveBos = new BufferedOutputStream(
        new FileOutputStream(receiveFile));
      BufferedOutputStream tempBos = new BufferedOutputStream(
        new FileOutputStream(tempFile));
      while ((length = dis.readInt()) != 0) {
        // 临时文件
        dis.read(buffer);
        tempBos.write(buffer, 0, length);
        // 解密文件
        deBuffer = aes.decryptText(buffer);
        receiveBos.write(deBuffer, 0, length);
      }
      // 接受完毕
      dos.writeUTF("ok");
      System.out.println("接受完毕");
      receiveBos.close();
      tempBos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Server server = new Server();
    server.start(8888);
  }
}