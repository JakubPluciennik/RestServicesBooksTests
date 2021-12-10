package pl.sggw;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class HtmlWriter {

  static List<Book> bookList = new ArrayList<>();
  static private int bookIndex = 1;

  public static class Autor {
    private String name;
    private String surname;
  }

  public static class Book {
    private int index;
    private String title;
    private Autor autor = new Autor();

    public Book(String title, String authorName, String authorSurname) {
      this.index = bookIndex;
      this.title = title;
      this.autor.name = authorName;
      this.autor.surname = authorSurname;

    }
  }

  /**
   * GET /books.html 0.1 - tabela z książkami, id, nazwa, autor - imię nazwisko, gdy w bazie nie ma książek ma wyświetlić komunikat "Brak książek"
   *
   * @param bufferedWriter wypisywanie na stronę
   * @throws IOException
   */
  public static void books(BufferedWriter bufferedWriter) throws IOException {
    bufferedWriter.write(Files.readString(HttpServer.books));
    if (bookList.isEmpty()) {
      bufferedWriter.write("<div>Brak książek</div>");
    } else {
      bufferedWriter.write(
          "<table>\n<tr>\n<th>ID</th>\n<th>Tytuł książki</th>\n<th>Imię autora</th>\n<th>Nazwisko autora</th>\n</tr>");
      for (Book book : bookList) {
        bufferedWriter.write(
            String.format("<tr>\n <td>%d</td>\n <td>%s</td>\n <td>%s</td>\n <td>%s</td>\n </tr>",
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
  public static boolean manage(BufferedWriter bufferedWriter) throws IOException {
    bufferedWriter.write(Files.readString(HttpServer.manage));

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
  public static void addBook(BufferedWriter bufferedWriter) throws IOException {
    bufferedWriter.write(Files.readString(HttpServer.addBook));
  }

  /**
   * Dodanie książki
   * @param s zapytanie POST z danymi do dodania książki
   * @return
   */
  public static synchronized boolean addBookAction(String s) {
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
  public static boolean updateBook(BufferedWriter bufferedWriter) throws IOException {
    bufferedWriter.write(Files.readString(HttpServer.updateBook));
    return true;
  }
}
