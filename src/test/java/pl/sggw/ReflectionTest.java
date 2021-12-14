package pl.sggw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReflectionTest {

  @Test
  void testSerializeJson() {
    String result = Reflection.serializeJson(new HtmlWriter());
    Assertions.assertEquals("replaceMeWithExpectedResult", result);
  }

  @Test
  void testDeserializeJson() {
    HtmlWriter result = Reflection.deserializeJson(null);
    Assertions.assertEquals(new HtmlWriter(), result);
  }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme