package pl.sggw;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public  class HtmlWriter {

  public HtmlWriter() {
  }

  List<Book> bookList = Collections.synchronizedList(new ArrayList<>());
  private int bookIndex = 1;
  private int updateIndex = -1;


  private Book getBook(int index) {
    Book wynik = null;
    try {
      for (Book b : bookList) {
        if (b.index == index) {
          wynik = b;
        }
      }
      if (wynik == null && index > bookIndex) {
        throw new IndexOutOfBoundsException();
      } else if (wynik == null && index < bookIndex) {
        throw new IllegalArgumentException();
      }
    } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
      e.printStackTrace();
    }
    return wynik;
  }

  private boolean setBook(Book book) {
    try {
      for (int i = 0; i < bookList.size(); i++) {
        Book tmp = bookList.get(i);
        if (tmp.index == book.index) {
          bookList.set(i, book);
        }
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof HtmlWriter)) {
      return false;
    }

    HtmlWriter that = (HtmlWriter) o;

    if (bookIndex != that.bookIndex) {
      return false;
    }
    if (updateIndex != that.updateIndex) {
      return false;
    }
    return bookList != null ? bookList.equals(that.bookList) : that.bookList == null;
  }

  @Override
  public int hashCode() {
    int result = bookList != null ? bookList.hashCode() : 0;
    result = 31 * result + bookIndex;
    result = 31 * result + updateIndex;
    return result;
  }


  public class Autor {
    private String name;
    private String surname;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Autor)) {
        return false;
      }

      Autor autor = (Autor) o;

      if (name != null ? !name.equals(autor.name) : autor.name != null) {
        return false;
      }
      return surname != null ? surname.equals(autor.surname) : autor.surname == null;
    }

    @Override
    public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (surname != null ? surname.hashCode() : 0);
      return result;
    }
  }

  public class Book {
    private int index;
    private String title;
    private final Autor autor = new Autor();

    public Book(String title, String authorName, String authorSurname) {
      this.index = bookIndex;
      this.title = title;
      this.autor.name = authorName;
      this.autor.surname = authorSurname;
    }

    public Book(String title, String authorName, String authorSurname, int index) {
      this.index = index;
      this.title = title;
      this.autor.name = authorName;
      this.autor.surname = authorSurname;
    }

    public Book() {
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Book)) {
        return false;
      }

      Book book = (Book) o;

      if (index != book.index) {
        return false;
      }
      if (title != null ? !title.equals(book.title) : book.title != null) {
        return false;
      }
      return autor != null ? autor.equals(book.autor) : book.autor == null;
    }

    @Override
    public int hashCode() {
      int result = index;
      result = 31 * result + (title != null ? title.hashCode() : 0);
      result = 31 * result + (autor != null ? autor.hashCode() : 0);
      return result;
    }
  }

   Book makeBook(String title, String authorName, String authorSurname, int index) {
    return new Book(title, authorName, authorSurname, index);
  }

  /**
   * GET /books.html 0.1 - tabela z książkami, id, nazwa, autor - imię nazwisko, gdy w bazie nie ma książek ma wyświetlić komunikat "Brak książek"
   *
   * @param bufferedWriter wypisywanie na stronę
   * @throws IOException
   */
  public void books(BufferedWriter bufferedWriter) {
    try {
      for (String s : Files.readAllLines(HttpServer.books)) {
        bufferedWriter.write(s);
      }
      if (bookList.isEmpty()) {
        bufferedWriter.write("<div>Brak książek</div>");
      } else {
        bufferedWriter.write("<table>\n<thead>\n<tr>\n<th class=\"id\">ID</th>\n<th class=\"title\">Tytuł książki</th>\n" +
            "<th class=\"authorName\">Imię autora</th>\n<th class=\"authorSurname\">Nazwisko autora</th></thead>\n</tr>");
        bufferedWriter.write("<tbody>");
        for (Book book : bookList) {
          bufferedWriter.write(String.format(
              "<tr>\n <td class=\"id\">%d</td>\n <td class=\"title\">%s</td>\n <td class=\"authorName\">%s</td>\n <td class=\"authorSurname\">%s</td>\n </tr>",
              book.index, book.title, book.autor.name, book.autor.surname));
        }
      }
      String endOfFile = "\n</tbody>\n</table>\n</main>\n</body>\n</html>";
      bufferedWriter.write(endOfFile);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * GET /manage.html 0.1 - wyświetla formularz wysyłający POST do /clearBooksAction ta akcja ma usunąć wszystkie książki,
   * zresetować sekwencję identyfikatorów do 1 i zrobić redirect do /books.html
   *
   * @param bufferedWriter wypisywanie na stronę
   * @return
   * @throws IOException
   */
  public boolean manage(BufferedWriter bufferedWriter) {
    try {
      for (String s : Files.readAllLines(HttpServer.manage)) {
        bufferedWriter.write(s);
      }
      return true;

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean clearBooksAction(String postInfo) {
    bookList.clear();
    bookIndex = 1;
    return true;
  }

  /**
   * GET /addBook.html 0.1 - formularz do dodawania książki wysyłany POST'em inputy
   * "title", "authorName" "authorSurname", po wysłaniu ma być redirect do /books.html
   *
   * @param bufferedWriter wypisywanie na stronę
   * @throws IOException
   */
  public void addBook(BufferedWriter bufferedWriter) {
    try {
      for (String s : Files.readAllLines(HttpServer.addBook)) {
        bufferedWriter.write(s);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Dodanie książki
   *
   * @param s zapytanie POST z danymi do dodania książki
   * @return
   */
  public boolean addBookAction(String s) {
    try {
      if (s.contains("=") && s.contains("&")) {
        String[] sArray = s.split("[&=]");
        String title = sArray[1];
        String authorName = sArray[3];
        String authorSurname = sArray[5];

        bookList.add(new Book(title, authorName, authorSurname));
        bookIndex++;
        return true;
      } else {
        throw new UnsupportedOperationException();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }


  /**
   * GET /updateBook.html?id=<id książki> 0.1 strona wyświetla formularz aktualizacji danych książki,
   * formularz wysyłany postem do /updateBookAction z redirect do /books.html
   *
   * @param bufferedWriter wypisywanie na stronę
   * @throws IOException
   */
  public void updateBook(BufferedWriter bufferedWriter, String indexS) {
    try {
      for (String s : Files.readAllLines(HttpServer.updateBook)) {
        bufferedWriter.write(s);
      }
      if (indexS.length() == 0) {
        throw new NullPointerException();
      } else if (Integer.parseInt(indexS) > bookIndex) {
        throw new IndexOutOfBoundsException();
      } else {
        int index = Integer.parseInt(indexS);
        Book tmp = getBook(index);
        bufferedWriter.write(String.format(
            "<label class=\"textInput\" for=\"title\"> Tytuł <input type=\"text\" name=\"title\" value=\"%s\"></label>\n" +
                "<label class=\"textInput\" for=\"authorName\"> Imię Autora<input type=\"text\" name=\"authorName\" value=\"%s\"></label>\n" +
                "<label class=\"textInput\" for=\"authorSurname\">Nazwisko Autora<input type=\"text\" name=\"authorSurname\" value=\"%s\"></label>\n" +
                "<input type=\"submit\" name=\"submit\" value=\"Edytuj\">", tmp.title, tmp.autor.name, tmp.autor.surname));

        String endOfFile = "\n</form>\n</main>\n</body>\n</html>";
        bufferedWriter.write(endOfFile);
        updateIndex = index;
      }
    } catch (Exception e) {
      try {
        e.printStackTrace();
        bufferedWriter.write("<h1 style=\"text-align:center; padding:20px;\">Podano zły indeks książki</h1>");
        bufferedWriter.write("<p style=\"padding-left:10px;\">Poprawne indeksy: \n");
        for (Book b : bookList) {
          bufferedWriter.write(b.index + ", ");
        }
        String endOfFile = "\n<p>\n</main>\n</body>\n</html>";
        bufferedWriter.write(endOfFile);
      } catch (IOException f) {
        f.printStackTrace();
      }
    }
  }

  public boolean updateBookAction(String s) {
    try {
      if (s.contains("=") && s.contains("&") && updateIndex < bookIndex) {
        String[] sArray = s.split("[&=]");
        String title = sArray[1];
        String authorName = sArray[3];
        String authorSurname = sArray[5];

        bookList.set(bookList.indexOf(getBook(updateIndex)), new Book(title, authorName, authorSurname, updateIndex));
        updateIndex = -1;
        return true;
      } else {
        throw new UnsupportedOperationException();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
