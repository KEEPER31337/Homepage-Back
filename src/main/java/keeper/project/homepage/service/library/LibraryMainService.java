package keeper.project.homepage.service.library;

import java.util.ArrayList;
import java.util.List;
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

  public Page<BookEntity> displayTenBooks(){

    Sort sort = Sort.by("id").descending();
    Pageable pageable = PageRequest.of(0, 10, sort);
    Page<BookEntity> bookEntityPage = bookRepository.findAll(pageable);

    return bookEntityPage;
  }
}
