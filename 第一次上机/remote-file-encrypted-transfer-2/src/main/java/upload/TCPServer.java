package upload;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import cipher.AES;
import cipher.DH;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

public class TCPServer {
  // 服务端公私钥
  private static byte[] publicKey1;
  private static byte[] privateKey1;
  // 客户端公钥
  private static byte[] publicKey2;
  // 会话密钥
  private static byte[] key1;

  // 服务端口
  private final int PORT = 8888;

  // 初始化公私钥对
  private void initKeyPair() {
    try {
      Map<String, Object> keyMap1 = DH.initKey(publicKey2);
      publicKey1 = DH.getPublicKey(keyMap1);
      privateKey1 = DH.getPrivateKey(keyMap1);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("服务端公钥:\n" + Base64.encodeBase64String(publicKey1));
    System.out.println("服务端私钥:\n" + Base64.encodeBase64String(privateKey1));
  }

  // 生成服务端密钥对
  private void initSessionKey() {
    try {
      key1 = DH.getSecretKey(publicKey2, privateKey1);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("会话密钥:\n" + Base64.encodeBase64String(key1));
  }

  // 初始化
  private void init(String receiveFilePath) throws IOException {
    ServerSocket server = new ServerSocket(PORT);

    while(true) {
      Socket socket = server.accept();
      System.out.println(
        "来访客户信息:\n" +
        "客户端IP：" + socket.getInetAddress() +
        " 客户端端口：" + socket.getInetAddress().getLocalHost() +
        "已连接服务器"
      );

      // DH 密钥交换
      // 接收客户端的公钥
      DataInputStream dis = new DataInputStream(socket.getInputStream());
      publicKey2 = new byte[dis.readInt()];
      dis.readFully(publicKey2);
      System.out.println("客户端公钥:\n" + Base64.encodeBase64String(publicKey2));

      // 利用客户端公钥初始化服务端公私钥对
      initKeyPair();

      // 将服务端公钥发给客户端
      DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      dos.writeInt(publicKey1.length);
      dos.write(publicKey1);

      // 生成会话密钥
      initSessionKey();

      // 获取 AES 解密对象
      Cipher cipher = AES.decrypt(Base64.encodeBase64String(key1));

      // 接受文件
      // 读取文件名
      String receiveFileName = dis.readUTF();
      System.out.println("接受文件名:" + receiveFileName);
      new Thread(new Runnable() {
        public void run() {
          try {
            InputStream is = socket.getInputStream();

            // 判断文件是否存在
            File file = new File(receiveFilePath + File.separator + receiveFileName);
            if(!file.exists()) {
              file.createNewFile();
            }

            // 接收待解密文件
            CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(file), cipher);
            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = is.read(bytes)) != -1) {
              cos.write(bytes,0,len);
            }

            // 告诉客户端上传成功
            socket.getOutputStream().write("上传成功".getBytes());
            // 资源释放
            cos.close();
            socket.close();
          } catch(IOException e) {
            e.printStackTrace();
          }
        }
      }).start();
    }
  }

  // 服务端程序
  public TCPServer(String receiveFilePath) throws IOException {
    init(receiveFilePath);
  }

  public static void main(String[] args) throws IOException {
    TCPServer server = new TCPServer(
      "src/main/resources/receiveFile"
    );
  }
}