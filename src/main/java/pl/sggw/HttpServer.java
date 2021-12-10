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
  static final Path index = Path.of("src/main/resources/index.html");
  static final Path books = Path.of("src/main/resources/books.html");
  static final Path manage = Path.of("src/main/resources/manage.html");
  static final Path addBook = Path.of("src/main/resources/addBook.html");
  static final Path updateBook = Path.of("src/main/resources/updateBook.html");
  static boolean status = true;

  public static void main(String[] args) throws IOException {

    ServerSocket serverSocket = new ServerSocket(8080);


    while (status) {
      listenAndServe(serverSocket);
    }
  }

  private static void listenAndServe(ServerSocket serverSocket) throws IOException {
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
        stringList.add(URLDecoder.decode(tmp, StandardCharsets.UTF_8.name()));
        tmp = "";
      } else {
        tmp += c;
      }
    }

    String getType = "";
    String action;
    String postInfo = URLDecoder.decode(tmp, StandardCharsets.UTF_8.name());
    if (postInfo.length() > 0 && stringList.size() > 20) {
      if (HtmlWriter.addBookAction(postInfo)) {
        System.out.println("Dodano pomyślnie książkę");
      }
    }

    //Write to Website
    bufferedWriter.write("HTTP/1.1 200 OK\n");
    bufferedWriter.write("Connection: close\n");
    bufferedWriter.write("Content-Type: text/html; charset=UTF-8 \n\n");


    //4 możliwości:
    //  - GET /books.html
    //  - GET /manage.html
    //  - GET /addBook.html
    //  - GET /updateBook.html ?id=[id]&
    if (!stringList.isEmpty() && stringList.get(0).contains("?")) {
      getType = stringList.get(0).substring(0, stringList.get(0).indexOf((int) '?'));


      action = getType.split("/")[1];

      switch (action) {
        case ("books.html") -> HtmlWriter.books(bufferedWriter);  //wypisanie książek
        case ("manage.html") -> HtmlWriter.manage(bufferedWriter);  //zarządzanie książkami
        case ("addBook.html") -> HtmlWriter.addBook(bufferedWriter);  //dodanie książki
        case ("updateBook.html") -> HtmlWriter.updateBook(bufferedWriter);  //edycja książki
        default -> {
        }
      }
    } else if (stringList.get(0).contains("POST")) {
      try {
        String[] stringArray = stringList.get(0).split("[ //]");
        if (stringArray.length > 3) {
          String postAction = stringArray[2];
          if (postAction.equals("books.html")) {
            HtmlWriter.books(bufferedWriter);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }


    } else {
      bufferedWriter.write(Files.readString(index));
    }

    bufferedWriter.flush();
    bufferedWriter.close();
  }
}
