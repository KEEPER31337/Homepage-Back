package keeper.project.homepage.repository.library;

import java.util.Optional;
import keeper.project.homepage.entity.library.BookBorrowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowRepository extends JpaRepository<BookBorrowEntity, Long> {

  Optional<BookBorrowEntity> findByTitleAndAuthorAndBorrowMemberId(String title, String author,
      Long BorrowMemberId);

}
