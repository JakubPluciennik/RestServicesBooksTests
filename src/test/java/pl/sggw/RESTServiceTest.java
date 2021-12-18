package pl.sggw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RESTServiceTest {
  static Properties prop;
  static String path = "";

  static {
    prop = new Properties();
    try {
      prop.load(new FileInputStream("src/main/resources/application.properties"));
    } catch (IOException e) {
      e.printStackTrace();
      try {
        prop.load(new FileInputStream("src/test/resources/application.properties"));
      } catch (IOException f) {
        f.printStackTrace();
        prop.put("port", "8080");
        prop.put("host", "http://localhost");
      }
    }
    path = prop.get("host") + ":" + prop.get("port");
  }

  @BeforeEach
  public void delete() throws IOException {
    URL url = new URL(path + "/books");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("DELETE");
    int responseCode = connection.getResponseCode();
    connection.disconnect();
  }


  @Test
  public void testResponseCodesValid() throws IOException {
    URL url = new URL(path + "/books");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("DELETE");
    int responseCode = connection.getResponseCode();
    connection.disconnect();
    Assertions.assertEquals(200, responseCode);

    connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    responseCode = connection.getResponseCode();
    Assertions.assertEquals(200, responseCode);
    connection.disconnect();

    connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("PUT");
    responseCode = connection.getResponseCode();
    Assertions.assertEquals(200, responseCode);
    connection.disconnect();

    connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    responseCode = connection.getResponseCode();
    Assertions.assertEquals(201, responseCode);
    connection.disconnect();
  }

  @Test
  public void testResponseCodesInvalid() throws IOException {
    URL url = new URL(path + "/books");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("HEAD");
    int responseCode = connection.getResponseCode();
    connection.disconnect();
    Assertions.assertEquals(404, responseCode);

    connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("OPTIONS");
    responseCode = connection.getResponseCode();
    Assertions.assertEquals(404, responseCode);
    connection.disconnect();
  }

  @Test
  public void testPOST() throws IOException {
    //dodanie książki za pomocą POST
    URL url = new URL(path + "/books");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json; utf-8");
    connection.setRequestProperty("Accept", "application/json");
    connection.setDoOutput(true);

    String title = "Tytuł";
    String authorName = "Imię";
    String authorSurname = "Nazwisko";
    String requestBody =  //Obiekt w Json
        "{\n\"title\":\"%s\",\n\"autor\":{\n\"name\": \"%s\",\n\"surname\":\"%s\"\n  }\n}".formatted(title, authorName, authorSurname);

    try (OutputStream os = connection.getOutputStream()) {
      byte[] input = requestBody.getBytes("utf-8");
      os.write(input, 0, input.length);
    }
    StringBuilder response;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
      response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }
    connection.disconnect();
    Assertions.assertEquals("1", response.toString());
  }

  @Test
  public void testPOST10() throws IOException {
    //dodanie książki za pomocą POST 10 razy
    URL url = new URL(path + "/books");
    for (int i = 1; i <= 10; i++) {


      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json; utf-8");
      connection.setRequestProperty("Accept", "application/json");
      connection.setDoOutput(true);

      String title = "Tytuł " + i;
      String authorName = "Imię " + i;
      String authorSurname = "Nazwisko " + i;
      String requestBody =  //Obiekt w Json
          "{\n\"title\":\"%s\",\n\"autor\":{\n\"name\": \"%s\",\n\"surname\":\"%s\"\n  }\n}".formatted(title, authorName, authorSurname);

      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = requestBody.getBytes("utf-8");
        os.write(input, 0, input.length);
      }
      StringBuilder response;
      try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
        response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
      }
      connection.disconnect();
      Assertions.assertEquals("" + i, response.toString());
    }
  }

  @Test
  public void testPUT() throws IOException {
    //edycja książki za pomocą PUT
    testPOST();
    URL url = new URL(path + "/books/1");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("PUT");
    connection.setRequestProperty("Content-Type", "application/json; utf-8");
    connection.setRequestProperty("Accept", "application/json");
    connection.setDoOutput(true);

    String title = "Tytuł - edytowany";
    String authorSurname = "Nazwisko - edytowane";
    String requestBody =  //Obiekt w Json
        "{\n\"title\":\"%s\",\n\"autor\":{\n\"surname\":\"%s\"\n  }\n}".formatted(title, authorSurname);

    try (OutputStream os = connection.getOutputStream()) {
      byte[] input = requestBody.getBytes("utf-8");
      os.write(input, 0, input.length);
    }
    StringBuilder response;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
      response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }
    connection.disconnect();
    Assertions.assertTrue(response.toString()
        .contains("Tytuł - edytowany") && response.toString()
        .contains("Nazwisko - edytowane"));
  }

  @Test
  public void testDELETE() throws IOException {
    //usuwanie wszystkich
    testPOST10();
    URL url = new URL(path + "/books");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("DELETE");
    connection.setRequestProperty("Content-Type", "application/json; utf-8");
    connection.setRequestProperty("Accept", "application/json");

    StringBuilder response;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
      response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }
    connection.disconnect();
    Assertions.assertTrue(response.toString().contains("ALL"));
  }

  @Test
  public void testDELETEid() throws IOException {
    //usuwanie z pomocą id
    testPOST10();
    URL url = new URL(path + "/books/4");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("DELETE");
    connection.setRequestProperty("Content-Type", "application/json; utf-8");
    connection.setRequestProperty("Accept", "application/json");

    StringBuilder response;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
      response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }
    connection.disconnect();
    Assertions.assertTrue(response.toString().contains("4"));
  }

  @Test
  public void testGET() throws IOException {
    //odczytanie wszystkich książek
    testPOST10();
    URL url = new URL(path + "/books");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "application/json; utf-8");
    connection.setRequestProperty("Accept", "application/json");

    StringBuilder response;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
      response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }
    connection.disconnect();
    String resp = response.toString();
    String[] c = resp.toString()
        .split("\"index\"");
    int occurrence = c.length - 1;
    Assertions.assertEquals(10, occurrence);
  }

  @Test
  public void testGETid() throws IOException {
    //odczytanie dodanej książki
    testPOST();
    URL url = new URL(path + "/books/1");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "application/json; utf-8");
    connection.setRequestProperty("Accept", "application/json");

    StringBuilder response;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
      response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
    }
    connection.disconnect();
    String resp = response.toString();
    String[] c = resp.toString()
        .split("\"index\"");
    int occurrence = c.length - 1;
    Assertions.assertEquals(1, occurrence);
  }
}
