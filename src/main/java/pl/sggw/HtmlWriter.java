package pl.sggw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HtmlWriter {

  public HtmlWriter() {
  }

  List<Book> bookList = Collections.synchronizedList(new ArrayList<>());
  private int bookIndex = 1;
  private final int updateIndex = -1;


  private Book getBook(int index) {
    Book wynik = null;
    for (Book b : bookList) {
      if (b.index == index) {
        wynik = b;
      }
    }
    if (wynik == null && index >= bookIndex) {
      throw new IndexOutOfBoundsException();
    } else if (wynik == null) {
      throw new IllegalArgumentException();
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

  //--------------METODY REST--------------
  public String getBookId(int id) {
    try {
      return Reflection.serializeJson(getBook(id));
    } catch (Exception e) {
      e.printStackTrace();
      return "Podano nieprawidłowy indeks";
    }
  }

  public String getAllBooks() {
    StringBuilder sb = new StringBuilder();
    if (bookList.size() > 0) {
      for (Book b : bookList) {
        sb.append(Reflection.serializeJson(b));
      }
    } else {
      sb.append("Brak książek.");
    }
    return sb.toString();
  }

  public boolean deleteBook(int id) {
    try {
      return bookList.remove(getBook(id));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deleteAllBooks() {
    return clearBooksAction("");
  }

  public String putBook(int id, List<String> changes) {
    try {
      Book b = getBook(id);
      for (int i = 0; i < changes.size(); i = i + 2) {
        if (changes.get(i)
            .equals("autor")) {
          i++;
        }
        switch (changes.get(i)) {
          case "title" -> b.title = changes.get(i + 1);
          case "name" -> b.autor.name = changes.get(i + 1);
          case "surname" -> b.autor.surname = changes.get(i + 1);
        }
      }
      setBook(b);
      return getBookId(id);
    } catch (Exception e) {
      return "Podano nieprawidłowy indeks";
    }
  }

  public String postBook(List<String> variables) {
    try {
      String title = variables.get(variables.indexOf("title") + 1);
      String authorName = variables.get(variables.indexOf("name") + 1);
      String authorSurname = variables.get(variables.indexOf("surname") + 1);
      int index = bookIndex;

      bookList.add(new Book(title, authorName, authorSurname));
      bookIndex++;
      return "" + index;
    } catch (Exception e) {
      e.printStackTrace();
      return "Zły format";
    }

  }
  //----------------------------------------


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
      if (!autor.name.equals(((Book) o).autor.name)) {
        return false;
      }
      return autor.surname.equals(((Book) o).autor.surname);
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


  public boolean clearBooksAction(String postInfo) {
    bookList.clear();
    bookIndex = 1;
    return true;
  }
}