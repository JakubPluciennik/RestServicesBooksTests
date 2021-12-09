package pl.sggw;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) throws IOException {
    Path path = Path.of("src/main/resources/index.html");
    System.out.println(Files.readString(path));
  }
}
