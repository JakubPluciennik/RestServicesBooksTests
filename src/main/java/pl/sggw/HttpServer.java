package pl.sggw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {
  static final Path index = Paths.get("src/main/resources/index.html");
  static final Path books = Paths.get("src/main/resources/books.html");
  static final Path manage = Paths.get("src/main/resources/manage.html");
  static final Path addBook = Paths.get("src/main/resources/addBook.html");
  static final Path updateBook = Paths.get("src/main/resources/updateBook.html");
  static final Path jsonPath = Paths.get("src/main/resources/data.json");
  static boolean status = true;
  static HtmlWriter htmlWriter = new HtmlWriter();

  public static void main(String[] args) throws IOException {

    ServerSocket serverSocket = new ServerSocket(8080);
    htmlWriter = Reflection.deserializeJson(jsonPath);

    while (status) {
      listenAndServe(serverSocket);
      String s = Reflection.serializeJson(htmlWriter);
      PrintWriter writer = new PrintWriter(jsonPath.toFile(), "UTF-8");
      writer.println(s);
      writer.close();

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

    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketOutputStream, "UTF-8"));
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketInputStream, "UTF-8"));

    //Read from Website
    List<String> stringList = new ArrayList<>();
    String tmp = "";
    while (bufferedReader.ready()) {
      char c = (char) bufferedReader.read();
      if (c == '\n') {
        stringList.add(URLDecoder.decode(tmp, "UTF-8"));
        tmp = "";
      } else {
        tmp += c;
      }
    }

    String action = "";
    String postInfo = URLDecoder.decode(tmp, "UTF-8");
    if (stringList.size() > 0 && stringList.get(0).contains("POST")) {
      if (!stringList.get(0).contains("Action") && postInfo.length() > 0) {  //dodanie książki
        if (htmlWriter.addBookAction(postInfo)) {
          System.out.println("Dodano pomyślnie książkę");
        } else {
          System.out.println("Nie udało się dodać książki");
        }
      } else {  // updateBookAction i clearBooksAction
        if (stringList.get(0).contains("update")) {
          boolean updateInfo = htmlWriter.updateBookAction(postInfo);
          if (updateInfo) {
            //redirect do books.html
            action = "redirect";
          } else {
            stringList.set(0, "GET /index.html HTTP/1.1");
          }
        } else if (stringList.get(0).contains("clear")) {
          boolean clearInfo = htmlWriter.clearBooksAction(postInfo);
          if (clearInfo) {
            //redirect do books.html
            action = "redirect";
          } else {
            stringList.set(0, "GET /index.html HTTP/1.1");
          }
        }
      }

    }
    for (String s : stringList) {
      System.out.println(s);
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
    if (action.equals("redirect")) {
      //redirect do books.html
      bufferedWriter.write("<!doctype html><html lang=\"pl\"><head><title>redirect</title><meta charset=\"utf-8\">" +
          "<meta http-equiv=\"refresh\" content=\"0; url=books.html\" /><style>\n body{\nfont-family: Helvetica;\n " +
          "background-color: #4f4f4f;\n}</style></head><body><p>Proszę zaczekać...</p></body></html>\n");
    } else if ( stringList.size() > 0 && stringList.get(0).contains(".html")) {
      String[] getArray = stringList.get(0).split("[ ?=&]");
      String getType = getArray[1];
      action = getType.split("/")[1];

      //wypisanie książek
      //zarządzanie książkami
      //dodanie książki
      //edycja książki
      switch (action) {
        case ("books.html"):
          htmlWriter.books(bufferedWriter);
          break;
        case ("manage.html"):
          htmlWriter.manage(bufferedWriter);
          break;
        case ("addBook.html"):
          htmlWriter.addBook(bufferedWriter);
          break;
        case ("updateBook.html"):
          htmlWriter.updateBook(bufferedWriter, getArray[3]);
          break;
        case ("index.html"):
          for (String s : Files.readAllLines(HttpServer.index)) {
            bufferedWriter.write(s);
          }
          break;
        default:
          bufferedWriter.write("<p>strona nie istnieje :(");
          break;
      }
    } else if (stringList.size() > 0 && stringList.get(0).contains("POST")) {
      try {
        String[] stringArray = stringList.get(0).split("[ /]");
        if (stringArray.length > 3) {
          String postAction = stringArray[2];
          if (postAction.equals("books.html")) {
            htmlWriter.books(bufferedWriter);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      for (String s : Files.readAllLines(HttpServer.index)) {
        bufferedWriter.write(s);
      }
    }

    bufferedWriter.flush();
    bufferedWriter.close();
  }
}
