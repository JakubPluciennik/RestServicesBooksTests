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
import java.util.ArrayList;
import java.util.List;

public class HttpServer {
  static final Path index = Path.of("src/main/resources/index.html");
  static final Path books = Path.of("src/main/resources/books.html");
  static final Path manage = Path.of("src/main/resources/manage.html");
  static final Path addBook = Path.of("src/main/resources/addBook.html");
  static final Path updateBook = Path.of("src/main/resources/updateBook.html");
  static final Path jsonPath = Path.of("src/main/resources/data.json");
  static boolean status = true;
  static HtmlWriter htmlWriter = new HtmlWriter();

  public void runServer() throws IOException {

    ServerSocket serverSocket = new ServerSocket(8080);
    try {
      htmlWriter = Reflection.deserializeJson(jsonPath);
    } catch (Exception e) {
      e.printStackTrace();
      htmlWriter = new HtmlWriter();
    }


    while (status) {
      listenAndServe(serverSocket);
      String s = Reflection.serializeJson(htmlWriter);
      PrintWriter writer = new PrintWriter(jsonPath.toFile(), StandardCharsets.UTF_8);
      writer.println(s);
      writer.close();
    }
  }

  private void listenAndServe(ServerSocket serverSocket) throws IOException {
    Socket socket = serverSocket.accept();

    Thread thread = new Thread(() -> serverRequest(socket));
    thread.start();
  }

  /**
   * Główna logika strony html
   * @param socket gniazdo na którym działa serwer
   * @throws IOException
   * @throws InterruptedException
   */
  private void serverRequest(Socket socket){
    try {
      InputStream socketInputStream = socket.getInputStream();
      OutputStream socketOutputStream = socket.getOutputStream();

      BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketOutputStream, StandardCharsets.UTF_8));
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketInputStream, StandardCharsets.UTF_8));

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

      String postInfo = URLDecoder.decode(tmp, StandardCharsets.UTF_8.name());

      String action = postInfoAction(stringList, postInfo);

      if (stringList.size() > 0) {
        System.out.println(stringList.get(0));
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
        redirect(bufferedWriter);

      } else if (stringList.size() > 0 && stringList.get(0).contains(".html")) {
        String[] getArray = stringList.get(0).split("[ ?=&]");
        String getType = getArray[1];
        action = getType.split("/")[1];

        //wypisanie książek
        //zarządzanie książkami
        //dodanie książki
        //edycja książki
        actionSwitch(action, bufferedWriter, getArray);

      } else {
        bufferedWriter.write(Files.readString(index));
      }

      bufferedWriter.flush();
      bufferedWriter.close();
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Wypisanie strony która została podana w String action
   *
   * @param action         string z akcją
   * @param bufferedWriter do wypisywania na stronę html
   * @param getArray       tablica z wydobytą akcją
   * @throws IOException
   */
  private void actionSwitch(String action, BufferedWriter bufferedWriter, String[] getArray) throws IOException {
    switch (action) {
      case ("books.html") -> htmlWriter.books(bufferedWriter);
      case ("manage.html") -> htmlWriter.manage(bufferedWriter);
      case ("addBook.html") -> htmlWriter.addBook(bufferedWriter);
      case ("updateBook.html") -> htmlWriter.updateBook(bufferedWriter, getArray[3]);
      case ("index.html") -> bufferedWriter.write(Files.readString(index));
      default -> bufferedWriter.write("<p>strona nie istnieje :(");
    }
  }

  /**
   * Tworzenie strony z redirectem do books.html
   *
   * @param bufferedWriter do wypisywania na stronę html
   */
  private void redirect(BufferedWriter bufferedWriter) {
    try {
      bufferedWriter.write("<!doctype html><html lang=\"pl\"><head><title>redirect</title><meta charset=\"utf-8\">" +
          "<meta http-equiv=\"refresh\" content=\"0; url=books.html\" /><style>\n body{\nfont-family: Helvetica;\n " +
          "background-color: #4f4f4f;\n}</style></head><body><p>Proszę zaczekać...</p></body></html>\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Ustalenie która akcja ma być wykonywana
   *
   * @param stringList lista z zapytaniem od klienta
   * @param postInfo   string z danymi z post
   * @return "redirect" jeśli akcja została wykonana lub ""
   */
  private String postInfoAction(List<String> stringList, String postInfo) {
    if (stringList.size() > 0 && stringList.get(0).contains("POST") && stringList.get(0).contains("Action")) {

      if (stringList.get(0).contains("add")) {
        boolean addInfo = htmlWriter.addBookAction(postInfo);
        if (addInfo) {
          //redirect do books.html
          return "redirect";
        }
      } else if (stringList.get(0).contains("update")) {
        boolean updateInfo = htmlWriter.updateBookAction(postInfo);
        if (updateInfo) {
          //redirect do books.html
          return "redirect";
        }
      } else if (stringList.get(0).contains("clear")) {
        boolean clearInfo = htmlWriter.clearBooksAction(postInfo);
        if (clearInfo) {
          //redirect do books.html
          return "redirect";
        }
      }
    }
    return "";
  }
}
