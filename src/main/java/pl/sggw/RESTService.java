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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class RESTService {
  static final Path jsonPath = Path.of("src/main/resources/data.json");
  static HtmlWriter htmlWriter = new HtmlWriter();
  static boolean status = true;

  public void runServer() throws IOException {

    ServerSocket serverSocketRest = new ServerSocket(8080);

    try {
      htmlWriter = Reflection.deserializeJson(jsonPath);
    } catch (Exception e) {
      e.printStackTrace();
      htmlWriter = new HtmlWriter();
    }
    while (status) {
      listenAndServe(serverSocketRest);
      String s = Reflection.serializeJson(htmlWriter);
      PrintWriter writer = new PrintWriter(jsonPath.toFile(), StandardCharsets.UTF_8);
      writer.println(s);
      writer.close();
    }
  }

  public void listenAndServe(ServerSocket serverSocket) throws IOException {
    Socket socket = serverSocket.accept();
    Thread thread = new Thread(() -> restRequest(socket));
    thread.start();
  }

  private void restRequest(Socket socket) {
    try {
      InputStream socketInputStream = socket.getInputStream();
      OutputStream socketOutputStream = socket.getOutputStream();

      BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketOutputStream, StandardCharsets.UTF_8));
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketInputStream, StandardCharsets.UTF_8));

      List<String> stringList = pageInfoToList(bufferedReader);


      String jsonData = buildJsonString(stringList);

      if (stringList.size() > 0) {
        System.out.println(stringList.get(0));  //typ operacji
      }
      doOperation(stringList, jsonData, bufferedWriter);

      bufferedWriter.flush();
      bufferedWriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private List<String> pageInfoToList(BufferedReader bufferedReader) throws IOException {
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
    stringList.add(tmp);

    return stringList;
  }

  private void doOperation(List<String> stringList, String jsonData, BufferedWriter bufferedWriter) {
    if (stringList.size() > 0 && stringList.get(0)
        .length() > 0) {
      String[] data = stringList.get(0)
          .replace("HTTP/1.1", "")
          .split("[ /]");
      String operation = data[0];

      try {
        jsonSwitch(operation, data, jsonData, bufferedWriter);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void jsonSwitch(String operation, String[] data, String jsonData, BufferedWriter bufferedWriter) throws IOException {

    //odpowiedź typu application/json

    //jeśli jedna z 4 operacji, odpowiedź serwera 200
    //w innym przypadku odpowiedź 404
    String response = "";
    switch (operation.toUpperCase(Locale.ROOT)) {
      case "GET" -> response = switchGetOption(bufferedWriter, data);
      case "POST" -> response = switchPostOption(bufferedWriter, data, jsonData);
      case "PUT" -> response = switchPutOperation(bufferedWriter, data, jsonData);
      case "DELETE" -> response = switchDeleteOption(bufferedWriter, data);
      default -> bufferedWriter.write("HTTP/1.1 404 Not Found\n");  //brak wspieranej operacji
    }
    bufferedWriter.write("Connection: close\n");
    bufferedWriter.write("Content-Type: application/json; charset=UTF-8 \n\n");
    bufferedWriter.write(response);
  }

  private String switchPutOperation(BufferedWriter bufferedWriter, String[] data, String jsonData) throws IOException {
    bufferedWriter.write("HTTP/1.1 200 OK\n");
    if (data.length > 4) {
      //deycja książki
      List<String> changes = new ArrayList<>();
      Arrays.stream(jsonData.replaceAll("[\\\\\\\"\\{\\}]", "  ")
              .replaceAll(" n ", "")
              .split("[,:]"))
          .toList()
          .forEach(s -> changes.add(s.strip()));
      int id = Integer.parseInt(data[3]);
      return htmlWriter.putBook(id, changes);
    } else {
      return "Brak indeksu.";
    }
  }
  private String switchDeleteOption(BufferedWriter bufferedWriter, String[] data) throws IOException {
    bufferedWriter.write("HTTP/1.1 200 OK\n");
    boolean confirm = false;
    if (data.length > 4) {
      //usunięcie książki z podanym id
      int id = Integer.parseInt(data[3]);
      confirm = htmlWriter.deleteBook(id);
      if (confirm) {
        return "DELETED: " + id;
      }
    } else {
      //usunięcie wszystkich książek
      confirm = htmlWriter.deleteAllBooks();
      if (confirm) {
        return "DELETED ALL";
      }
    }
    return "Błąd podczas usuwania";
  }
  private String switchPostOption(BufferedWriter bufferedWriter, String[] data, String jsonData) throws IOException {
    bufferedWriter.write("HTTP/1.1 200 OK\n");
    //dodanie książki z jsonData
    List<String> variables = new ArrayList<>();
    Arrays.stream(jsonData.replaceAll("[\\\\\\\"\\{\\}]", "  ")
            .replaceAll(" n ", "")
            .split("[,:]"))
        .toList()
        .forEach(s -> variables.add(s.strip()));

    return htmlWriter.postBook(variables);
  }
  private String switchGetOption(BufferedWriter bufferedWriter, String[] data) throws IOException {
    bufferedWriter.write("HTTP/1.1 200 OK\n");
    if (data.length > 4) {
      //wypisanie książki z id get
      int id = Integer.parseInt(data[3]);
      return htmlWriter.getBookId(id);
    } else {
      //wypisanie książek get
      return htmlWriter.getAllBooks();
    }
  }

  private String buildJsonString(List<String> stringList) { //pobieranie tekstu Json z POST do dodania książki
    StringBuilder jsonBuilder = new StringBuilder();
    if (stringList.contains("{")) {
      int jsonStart = stringList.indexOf("{");
      for (int i = jsonStart; i < stringList.size(); i++) {
        jsonBuilder.append(stringList.get(i));
      }
    } else if (stringList.get(stringList.size() - 1)
        .length() > 2) {
      jsonBuilder.append(stringList.get(stringList.size() - 1));
    }
    return jsonBuilder.toString();
  }
}
