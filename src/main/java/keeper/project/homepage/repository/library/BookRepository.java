package keeper.project.homepage.repository.library;

import java.util.Optional;
import keeper.project.homepage.entity.library.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

  Optional<BookEntity> findByTitleAndAuthor(String title, String author);

}

