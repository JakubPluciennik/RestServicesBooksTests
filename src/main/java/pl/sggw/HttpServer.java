package pl.sggw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {
  final static Path post = Path.of("src/main/resources/index.html");
  static boolean status = true;
  public static void main(String[] args) throws IOException {

    ServerSocket serverSocket = new ServerSocket(8080);


    while (status) {
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

    BufferedWriter bufferedWriter =
        new BufferedWriter(new OutputStreamWriter(socketOutputStream, StandardCharsets.UTF_8));
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(socketInputStream, StandardCharsets.UTF_8));

    //Read from Website
    List<String> stringList = new ArrayList<>();
    String tmp = "";
    while (bufferedReader.ready()) {
      char c = (char) bufferedReader.read();
      if (c == '\n') {
        stringList.add(tmp);
        tmp = "";
      } else {
        tmp += c;
      }
    }

    //ostatnia linia z zapytaniem z Formularza
    String formData = URLDecoder.decode(tmp, StandardCharsets.UTF_8.name());

    /*
    for (String s:stringList) {
      System.out.println(s);
    }
    */

    //Operacje POST, GET, PUT, DELETE
    if (stringList.get(0).startsWith("POST")) {
      System.out.println(Reflection.addBook(formData));
    }


    //Write to Website
    bufferedWriter.write("HTTP/1.1 200 OK\n");
    bufferedWriter.write("Connection: close\n");
    bufferedWriter.write("Content-Type: text/html; charset=UTF-8 \n\n");

    bufferedWriter.write(Files.readString(post));

    bufferedWriter.flush();
    bufferedWriter.close();
  }
}
