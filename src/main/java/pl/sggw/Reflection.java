package pl.sggw;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Reflection {
  List<Book> books = new ArrayList<>();

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
  static boolean addBook(String s){
    try{
      String[] sArray = s.split("[&=]");
      String method = sArray[1];
      String authorName = sArray[3];
      String authorSurname = sArray[5];
      String bookTitle = sArray[7];

      System.out.printf("%s | %s | %s | %s",method,authorName, authorSurname, bookTitle);
      return true;
    } catch(Exception e){
      e.printStackTrace();
      return false;
    }

  }
}
