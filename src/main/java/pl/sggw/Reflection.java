package pl.sggw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Reflection {


  public static void main(String[] args) throws IllegalAccessException {

  }

  private static List<String> getFieldNames(Field[] fields) {
    List<String> fieldNames = new ArrayList<>();
    for (Field field : fields) {
      field.setAccessible(true);
      fieldNames.add(field.getName());
    }
    return fieldNames;
  }

}
