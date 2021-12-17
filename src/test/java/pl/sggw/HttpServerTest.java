package pl.sggw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpServerTest {
  HtmlWriter htmlWriter = new HtmlWriter();

  @Test
  void testEqualsTrue() {
    htmlWriter.bookList.add(htmlWriter.makeBook("Tytuł 1", "Imię 1", "Nazwisko 1", 2));
    HtmlWriter htmlWriter1 = new HtmlWriter();
    htmlWriter1.bookList.add(htmlWriter.makeBook("Tytuł 1", "Imię 1", "Nazwisko 1", 2));
    Assertions.assertTrue(htmlWriter1.equals(htmlWriter));
  }

  @Test
  void testEqualsFalse() {
    htmlWriter.bookList.add(htmlWriter.makeBook("Tytuł 1", "Imię 1", "Nazwisko 1", 2));
    HtmlWriter htmlWriter1 = new HtmlWriter();
    htmlWriter1.bookList.add(htmlWriter.makeBook("Tytuł 2", "Imię 2", "Nazwisko 2", 5));
    Assertions.assertFalse(htmlWriter1.equals(htmlWriter));
  }

  @Test
  void testHashCode() {
    htmlWriter = new HtmlWriter();
    int result = htmlWriter.hashCode();
    Assertions.assertEquals(new HtmlWriter().hashCode(), result);
  }

  @Test
  void testMakeBook() {
    HtmlWriter.Book result = htmlWriter.makeBook("title", "authorName", "authorSurname", 3);
    Assertions.assertEquals(new HtmlWriter().new Book("title", "authorName", "authorSurname", 3), result);
  }
}
//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme