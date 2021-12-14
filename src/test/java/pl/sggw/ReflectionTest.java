package pl.sggw;

import com.google.gson.Gson;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReflectionTest {

  HtmlWriter htmlWriter = new HtmlWriter();
  Gson g = new Gson();

  @Test
  void testSerializeJsonEmpty() {
    String expected = g.toJson(htmlWriter);
    String result = Reflection.serializeJson(htmlWriter);

    Assertions.assertEquals(expected, result.replace("\n", ""));
  }

  @Test
  void testSerializeJson1Book() {
    htmlWriter = new HtmlWriter();
    htmlWriter.bookList.add(htmlWriter.makeBook("Tytuł,", "Imię", "Nazwisko", 1));
    String expected = g.toJson(htmlWriter);
    String result = Reflection.serializeJson(htmlWriter);

    Assertions.assertEquals(expected, result.replace("\n", ""));
  }

  @Test
  void testSerializeJson5Books() {
    htmlWriter = new HtmlWriter();
    for (int i = 0; i < 5; i++) {
      htmlWriter.bookList.add(htmlWriter.makeBook("Tytuł "+i+"", "Imię "+i+"", "Nazwisko "+i+"", i));
    }

    String expected = g.toJson(htmlWriter);
    String result = Reflection.serializeJson(htmlWriter);

    Assertions.assertEquals(expected, result.replace("\n", ""));
  }

  @Test
  void testSerializeJson100Books() {
    htmlWriter = new HtmlWriter();
    for (int i = 0; i < 100; i++) {
      htmlWriter.bookList.add(htmlWriter.makeBook("Tytuł "+i+"", "Imię "+i+"", "Nazwisko "+i+"", i));
    }

    String expected = g.toJson(htmlWriter);
    String result = Reflection.serializeJson(htmlWriter);

    Assertions.assertEquals(expected, result.replace("\n", ""));
  }

  @Test
  void testDeserializeJsonEmpty() {
    htmlWriter = new HtmlWriter();
    String test = Reflection.serializeJson(htmlWriter);

    AssertionsForClassTypes.assertThat(htmlWriter.equals(Reflection.deserializeJson(test))).isTrue();
  }

  @Test
  void testDeserializeJson1Book() {
    htmlWriter = new HtmlWriter();
    htmlWriter.bookList.add(htmlWriter.makeBook("Tytuł,", "Imię", "Nazwisko", 1));
    String test = Reflection.serializeJson(htmlWriter);

    AssertionsForClassTypes.assertThat(htmlWriter.equals(Reflection.deserializeJson(test))).isTrue();
  }

  @Test
  void testDeserializeJson5Books() {
    htmlWriter = new HtmlWriter();
    for (int i = 0; i < 5; i++) {
      htmlWriter.bookList.add(htmlWriter.makeBook("Tytuł "+i+"", "Imię "+i+"", "Nazwisko "+i+"", i));
    }
    String test = Reflection.serializeJson(htmlWriter);

    AssertionsForClassTypes.assertThat(htmlWriter.equals(Reflection.deserializeJson(test))).isTrue();
  }

  @Test
  void testDeserializeJson100Books() {
    htmlWriter = new HtmlWriter();
    for (int i = 0; i < 100; i++) {
      htmlWriter.bookList.add(htmlWriter.makeBook("Tytuł "+i+"", "Imię "+i+"", "Nazwisko "+i+"", i));
    }
    String test = Reflection.serializeJson(htmlWriter);

    AssertionsForClassTypes.assertThat(htmlWriter.equals(Reflection.deserializeJson(test))).isTrue();
  }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme