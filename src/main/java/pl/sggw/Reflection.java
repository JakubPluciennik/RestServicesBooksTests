package pl.sggw;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Reflection {

  /**
   * Metoda do serializacji Objektu HtmlWriter za pomocą relfeksji.
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
      if(object instanceof HtmlWriter.Book){
        tmp = tmp.replaceAll("}\\n}\\n}","}\n}");
      }
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

  //używanie biblioteki GSon, nie udało mi się refleksjami
  public static HtmlWriter deserializeJson(Path jsonPath) {
    HtmlWriter htmlWriter = new HtmlWriter();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonPath.toString()), StandardCharsets.UTF_8));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return deserializeJson(reader.lines().collect(Collectors.joining("\n")));
  }

  public static HtmlWriter deserializeJson(String json) {
    HtmlWriter htmlWriter;
    Gson g = new Gson();
    htmlWriter = g.fromJson(json, HtmlWriter.class);
    return htmlWriter;
  }
}

