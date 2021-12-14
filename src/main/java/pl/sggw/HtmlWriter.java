package pl.sggw;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class HtmlWriter {
  public HtmlWriter() {
  }

  List<Book> bookList = new ArrayList<>();
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


  public class Autor {
    private String name;
    private String surname;
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
  }

  /**
   * GET /books.html 0.1 - tabela z książkami, id, nazwa, autor - imię nazwisko, gdy w bazie nie ma książek ma wyświetlić komunikat "Brak książek"
   *
   * @param bufferedWriter wypisywanie na stronę
   * @throws IOException
   */
  public void books(BufferedWriter bufferedWriter) throws IOException {
    bufferedWriter.write(Files.readString(HttpServer.books));
    if (bookList.isEmpty()) {
      bufferedWriter.write("<div>Brak książek</div>");
    } else {
      bufferedWriter.write("<table>\n<tr>\n<th class=\"id\">ID</th>\n<th class=\"title\">Tytuł książki</th>\n" +
          "<th class=\"authorName\">Imię autora</th>\n<th class=\"authorSurname\">Nazwisko autora</th>\n</tr>");
      for (Book book : bookList) {
        bufferedWriter.write(String.format(
            "<tr>\n <td class=\"id\">%d</td>\n <td class=\"title\">%s</td>\n <td class=\"authorName\">%s</td>\n <td class=\"authorSurname\">%s</td>\n </tr>",
            book.index, book.title, book.autor.name, book.autor.surname));
      }
    }

    String endOfFile = "\n</table>\n</main>\n</body>\n</html>";
    bufferedWriter.write(endOfFile);
  }

  /**
   * GET /manage.html 0.1 - wyświetla formularz wysyłający POST do /clearBooksAction ta akcja ma usunąć wszystkie książki,
   * zresetować sekwencję identyfikatorów do 1 i zrobić redirect do /books.html
   *
   * @param bufferedWriter wypisywanie na stronę
   * @return
   * @throws IOException
   */
  public boolean manage(BufferedWriter bufferedWriter) throws IOException {
    bufferedWriter.write(Files.readString(HttpServer.manage));

    return true;
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
   * @return
   * @throws IOException
   */
  public void addBook(BufferedWriter bufferedWriter) throws IOException {
    bufferedWriter.write(Files.readString(HttpServer.addBook));
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
   * @return
   * @throws IOException
   */
  public void updateBook(BufferedWriter bufferedWriter, String indexS) throws IOException {
    try {
      bufferedWriter.write(Files.readString(HttpServer.updateBook));
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
      e.printStackTrace();
      bufferedWriter.write("<h1 style=\"text-align:center; padding:20px;\">Podano zły indeks książki</h1>");
      bufferedWriter.write("<p style=\"padding-left:10px;\">Poprawne indeksy: \n");
      for (Book b : bookList) {
        bufferedWriter.write(b.index + ", ");
      }
      String endOfFile = "\n<p>\n</main>\n</body>\n</html>";
      bufferedWriter.write(endOfFile);
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
