package keeper.project.homepage.service.library;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.repository.library.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryMainService {

  private final BookRepository bookRepository;


  public List<BookEntity> displayTenBooks(Pageable pageable) {

    List<BookEntity> bookEntityPage = bookRepository.findAll(pageable).getContent();

    return bookEntityPage;
  }

  public List<BookEntity> searchBooks(String keyword, Pageable pageable) {
    List<BookEntity> bookEntitiesTitle = bookRepository.findByTitleContaining(keyword, pageable);
    List<BookEntity> bookEntitiesAuthor = bookRepository.findByAuthorContaining(keyword, pageable);
    List<BookEntity> bookEntitiesInformation = bookRepository.findByInformationContaining(keyword,
        pageable);

    Set<BookEntity> bookEntitySet = new HashSet<>(bookEntitiesTitle);
    bookEntitySet.addAll(bookEntitiesAuthor);
    bookEntitySet.addAll(bookEntitiesInformation);

    List<BookEntity> bookEntities = new ArrayList<>(bookEntitySet);

    return bookEntities;
  }

  public BookEntity selectedBook(String title, String author) {
    return bookRepository.findByTitleAndAuthor(title, author).get();
  }
}
