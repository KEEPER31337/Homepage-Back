package keeper.project.homepage.repository;

import java.util.Optional;
import keeper.project.homepage.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

  Optional<BookEntity> findByTitle(String title);

}