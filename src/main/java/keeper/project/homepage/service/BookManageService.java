package keeper.project.homepage.service;

import java.util.Date;
import keeper.project.homepage.entity.BookEntity;
import keeper.project.homepage.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookManageService {

  private final BookRepository bookRepository;

  @Autowired
  public BookManageService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  /**
   * 도서 등록
   */
  public String addBook(String title,
      String author,
      String picture,
      String information,
      Long quantity) {

    BookEntity bookEntity = new BookEntity();
    Long nowTotal = 0L;

    if (bookEntity.getTitle() != null) {
      nowTotal = bookEntity.getTotal();
      System.out.println(nowTotal);
    }

    if (quantity + nowTotal > 4) { //동일한 책은 최대 4권까지 가능
      return "최대 수량을 초과 했습니다.";
    }

    Date registerDate = new Date();
    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .picture(picture)
            .information(information)
            .total(quantity)
            .borrow(0L)
            .enable(quantity + nowTotal)
            .registerDate(registerDate)
            .build());
    return "추가되었습니다";
  }

}
