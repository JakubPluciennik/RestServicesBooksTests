package pl.sggw;


public class Main {
  public static void main(String[] args) {
    HtmlWriter htmlWriter = new HtmlWriter();
    String test = Reflection.serializeJson(htmlWriter);

    HtmlWriter h2 = Reflection.deserializeJson(test);
    String s1 = "Test";
    String s2 = "Test";
    System.out.println(s1.equals(s2));
  }
}
