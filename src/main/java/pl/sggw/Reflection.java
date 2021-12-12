package pl.sggw;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class Reflection {

  /**
   * Metoda do serializacji Objektu HtmlWriter za pomocą relfeksji. (teoretycznie do każdego obiektu, w teorii nie sprawdzałem)
   *
   * @param object obiekt do serializacji
   * @param <T>    typ obketu
   * @return String z obiektem w postaci pliku Json
   */
  public static <T> String serializeJson(T object) {
    StringBuilder sb = new StringBuilder();
    String tmp = "";
    sb.append("{\n");
    try {
      List<Object> visited = new ArrayList<>();
      serializeJsonRec(object, visited, sb);
      sb.append("}");
      tmp = sb.toString();
      tmp = tmp.replaceAll("\n,", ",\n");
      tmp = tmp.replaceAll(",\n}", "\n}");
      tmp = tmp.replaceAll("\\{" + "\n" + "]", "]");
      tmp = tmp.replaceAll("}\n\\{", "},\n{");
      tmp = tmp.replaceAll("\\{" + "\n" + "\\[", "[\n{");
      tmp = tmp.replaceAll("\\[\n\\{\n]", "[]");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tmp;
  }

  private static <T> void serializeJsonRec(T object, List<Object> visited, StringBuilder sb) throws IllegalAccessException {
    //warunek stopu
    if (object.getClass().toString().contains("java.lang.String") || object.getClass().toString().contains("java.lang.Integer") ||
        visited.contains(object)) {
      sb.append(",");
      return;
    }
    visited.add(object);

    Field[] fields = object.getClass().getDeclaredFields();
    if (object instanceof List) {
      sb.append("[\n");
      for (Object o : (List<?>) object) {
        serializeJsonRec(o, visited, sb);
        sb.append("{\n");
      }
      sb.append("],\n");
    } else {
      for (Field f : fields) {
        f.setAccessible(true);
        String attributeName = f.getName();
        if (attributeName.equals("this$0")) {
          sb.append("}\n");
        } else {
          Object attributeValue = f.get(object);
          if (attributeValue instanceof List || attributeValue.toString().contains("HtmlWriter$")) {
            sb.append("\"" + attributeName + "\":{\n");
          } else {
            sb.append("\"" + attributeName + "\":");
            if (attributeValue instanceof Integer) {
              sb.append(attributeValue).append("\n");
            } else if (attributeValue instanceof String) {
              sb.append("\"").append(attributeValue).append("\"\n");
            }
          }
          serializeJsonRec(attributeValue, visited, sb);
          f.setAccessible(false);
        }
      }
    }
  }

  public static <T> T deserializeJson(T object) {

    return (T)new Object();
  }
}

