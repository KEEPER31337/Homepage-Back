package keeper.project.homepage.repository;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.BookEntity;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

  Optional<BookEntity> findByTitle(String title);

}

