package keeper.project.homepage.service;

import java.util.Date;
import keeper.project.homepage.entity.BookEntity;
import keeper.project.homepage.repository.BookRepository;
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
    if (bookRepository.findByTitle(title).isPresent()) {
      nowTotal = bookRepository.findByTitle(title).get().getTotal();
    }

    if (quantity + nowTotal > MAXIMUM_ALLOWD_BOOK_NUMBER) {
      return -1L;
    }

    return nowTotal + quantity;
  }

  /**
   * 도서 추가
   */
  public void addBook(String title, String author, String picture, String information, Long total) {
    Long borrowState = 0L;
    if (bookRepository.findByTitle(title).isPresent()) {
      borrowState = bookRepository.findByTitle(title).get().getBorrow();
    }
    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .picture(picture)
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
  public boolean isExist(String title, Long quantity) {

    if (!bookRepository.findByTitle(title).isPresent()) {
      return false;
    }
    return true;
  }

  public void updateDeleteInformation(String title, Long quantity) {
    String author = bookRepository.findByTitle(title).get().getAuthor();
    String picture = bookRepository.findByTitle(title).get().getPicture();
    String information = bookRepository.findByTitle(title).get().getInformation();
    Long borrow = bookRepository.findByTitle(title).get().getBorrow();
    Long total = bookRepository.findByTitle(title).get().getTotal();
    Long enable = bookRepository.findByTitle(title).get().getEnable();

    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .picture(picture)
            .information(information)
            .total(total - quantity)
            .borrow(borrow)
            .enable(enable - quantity)
            .registerDate(new Date())
            .build());
  }

}
