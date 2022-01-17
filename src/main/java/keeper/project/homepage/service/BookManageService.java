package keeper.project.homepage.service;

import java.awt.print.Book;
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
   * @param title
   * @param author
   * @param picture
   * @param information
   * @param quantity
   */
  public void addBook(String title, String author, String picture, String information,
      Long quantity) {

    Date registerDate = new Date();
    bookRepository.save(BookEntity.builder()
        .title(title)
        .author(author)
        .picture(picture)
        .information(information)
        .total(quantity)
        .borrow(0L)
        .enable(quantity)
        .registerDate(registerDate)
        .build());
  }

  /**
   * 도서 삭제
   * @return
   */
  public String deleteBook(String title, Long quantity){
    BookEntity bookEntity = new BookEntity();

    if(bookEntity.getTitle() == null){
      throw new RuntimeException("존재하지 않는 책입니다");
    }
    Long total = bookEntity.getTotal();

    if(total-quantity > 0){
      bookRepository.save(BookEntity.builder()
          .title(title)
          .total(quantity).build());
    }else{
      bookRepository.delete(BookEntity.builder().build());
    }
    return "삭제되었습니다";
  }

}
