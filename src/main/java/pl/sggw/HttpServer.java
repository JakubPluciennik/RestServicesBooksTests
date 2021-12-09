package pl.sggw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;

public class HttpServer {
  final static Path post = Path.of("src/main/resources/index.html");

  public static void main(String[] args) throws IOException, InterruptedException {

    ServerSocket serverSocket = new ServerSocket(8080);

    while (true) {
      listenAndServe(serverSocket);
    }
  }

  private static void listenAndServe(ServerSocket serverSocket)
      throws IOException {
    Socket socket = serverSocket.accept();

    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          serverRequest(socket);
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
  }

  private static void serverRequest(Socket socket) throws IOException, InterruptedException {
    InputStream socketInputStream = socket.getInputStream();
    OutputStream socketOutputStream = socket.getOutputStream();

    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketOutputStream, Charset.defaultCharset()));
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketInputStream, Charset.defaultCharset()));

    //Read from Website
    StringBuilder sb = new StringBuilder();
    while (bufferedReader.ready()) {
      sb.append((char)bufferedReader.read());
    }
    System.out.println(sb.toString());


    //Write to Website
    bufferedWriter.write("HTTP/1.1 200 OK\n");
    bufferedWriter.write("Connection: close\n");
    bufferedWriter.write("Content-Type: text/html; charset=utf-8\n\n");

    bufferedWriter.write(Files.readString(post));
    bufferedWriter.write("Zażółć gęśą jaźń"+"  </body>\n" +
        "</html>\n");

    bufferedWriter.flush();
    bufferedWriter.close();
  }
}
