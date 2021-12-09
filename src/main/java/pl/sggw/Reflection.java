package pl.sggw;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Reflection {

  public static class Autor {
    private String name;
    private String surname;
  }

  public static class Book {
    private String title;
    private Autor autor;
  }

  public static void main(String[] args) throws IllegalAccessException {
    Object person = new Autor();
    ((Autor) person).surname = "Kowalski";
    ((Autor) person).name = "Jan";
    Field[] fields = person.getClass().getDeclaredFields();

    List<String> actualFieldNames = getFieldNames(fields);
    for (String field : actualFieldNames) {
      System.out.println("Field: " + field);
    }

    for (Field f : fields) {
      boolean accessible = f.isAccessible();
      Object value = f.get(person);
      System.out.println("value=" + value);

      f.setAccessible(accessible);
    }
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
