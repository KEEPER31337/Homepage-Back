package keeper.project.homepage.repository.library;

import com.sun.source.doctree.UnknownBlockTagTree;
import java.sql.Date;
import java.util.List;
import keeper.project.homepage.entity.library.BookBorrowEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowRepository extends JpaRepository<BookBorrowEntity, Long> {

  List<BookBorrowEntity> findAllByExpireDateBetween(Pageable pageable, Date start, Date end);
}
