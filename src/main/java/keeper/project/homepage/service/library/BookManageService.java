package keeper.project.homepage.service.library;

import java.util.Date;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.repository.library.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookManageService {

  private final BookRepository bookRepository;
  private final BookEntity bookEntity;
  private static final Integer MAXIMUM_ALLOWD_BOOK_NUMBER = 4;

  @Autowired
  public BookManageService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
    bookEntity = new BookEntity();
  }

  /**
   * 도서 최대 권수 체크
   */
  public Long isCanAdd(String title, String author, Long quantity) {

    Long nowTotal = 0L;
    if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      nowTotal = bookRepository.findByTitleAndAuthor(title, author).get().getTotal();
    }

    if (quantity + nowTotal > MAXIMUM_ALLOWD_BOOK_NUMBER) {
      return -1L;
    }

    return nowTotal + quantity;
  }

  /**
   * 도서 추가
   */
  public void addBook(String title, String author, String information, Long total) {
    Long borrowState = 0L;
    if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      borrowState = bookRepository.findByTitleAndAuthor(title, author).get().getBorrow();
    }
    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .information(information)
            .total(total)
            .borrow(borrowState)
            .enable(total)
            .registerDate(new Date())
            .build());
  }

  /**
   * 도서 삭제가 가능한지 체크
   */
  public boolean isExist(String title, String author) {

    if (!bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      return false;
    }
    return true;
  }

  /**
   * 도서 삭제 업데이트
   */
  public void updateDeleteInformation(String title, String author, Long quantity) {
    String information = bookRepository.findByTitleAndAuthor(title, author).get().getInformation();
    Long borrow = bookRepository.findByTitleAndAuthor(title, author).get().getBorrow();
    Long total = bookRepository.findByTitleAndAuthor(title, author).get().getTotal();
    Long enable = bookRepository.findByTitleAndAuthor(title, author).get().getEnable();

    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .information(information)
            .total(total - quantity)
            .borrow(borrow)
            .enable(enable - quantity)
            .registerDate(new Date())
            .build());
  }

}
