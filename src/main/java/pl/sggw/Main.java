package pl.sggw;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    HttpServer httpServer = new HttpServer();
    try {
      httpServer.runServer();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
