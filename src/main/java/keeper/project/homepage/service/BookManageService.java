package keeper.project.homepage.service;

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
  public boolean isNotMax(String title, Long quantity) {

    Long nowTotal = 0L;
    if (bookRepository.findByTitle(title).isPresent()) {
      nowTotal = bookRepository.findByTitle(title).get().getTotal();
    }

    if (quantity + nowTotal > MAXIMUM_ALLOWD_BOOK_NUMBER) {
      return false;
    }

    return true;
  }

  /**
   * 도서 삭제가 가능한지 체크
   */
  public boolean isCanDelete(String title, Long quantity) {

    if (!bookRepository.findByTitle(title).isPresent()) {
      return false;
    }
    return true;
  }

}
