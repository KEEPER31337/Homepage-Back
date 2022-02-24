package keeper.project.homepage.user.service.library;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import keeper.project.homepage.dto.library.BookResult;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.exception.library.CustomBookNotFoundException;
import keeper.project.homepage.repository.library.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryMainService {

  private final BookRepository bookRepository;


  public List<BookResult> displayTenBooks(Pageable pageable) {

    List<BookEntity> bookEntityPage = bookRepository.findAll(pageable).getContent();

    return getBookResults(bookEntityPage);
  }


  public List<BookResult> searchBooks(String keyword, Pageable pageable) {
    List<BookEntity> bookEntitiesTitle = bookRepository.findByTitleContaining(keyword, pageable);
    List<BookEntity> bookEntitiesAuthor = bookRepository.findByAuthorContaining(keyword, pageable);
    List<BookEntity> bookEntitiesInformation = bookRepository.findByInformationContaining(keyword,
        pageable);

    Set<BookEntity> bookEntitySet = new HashSet<>(bookEntitiesTitle);
    bookEntitySet.addAll(bookEntitiesAuthor);
    bookEntitySet.addAll(bookEntitiesInformation);

    List<BookEntity> bookEntities = new ArrayList<>(bookEntitySet);

    return getBookResults(bookEntities);
  }

  public BookResult selectedBook(String title, String author) {
    BookEntity book = bookRepository.findByTitleAndAuthor(title, author).orElseThrow(
        CustomBookNotFoundException::new);

    return getBookResult(book);
  }

  private BookResult getBookResult(BookEntity book) {
    BookResult bookDto = new BookResult();
    bookDto.initWithEntity(book);
    return bookDto;
  }

  private List<BookResult> getBookResults(List<BookEntity> bookEntityPage) {
    List<BookResult> bookDtoList = new ArrayList<>();
    for (BookEntity book : bookEntityPage) {
      BookResult bookDto = getBookResult(book);
      bookDtoList.add(bookDto);
    }
    return bookDtoList;
  }
}
