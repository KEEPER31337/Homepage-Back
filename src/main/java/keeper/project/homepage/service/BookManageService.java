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


}
