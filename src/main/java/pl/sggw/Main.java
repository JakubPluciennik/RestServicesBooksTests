package pl.sggw;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {

    RESTService restService = new RESTService();
    try {
      restService.runServer();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
