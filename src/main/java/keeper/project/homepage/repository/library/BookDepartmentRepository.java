package keeper.project.homepage.repository.library;

import java.util.Optional;
import keeper.project.homepage.entity.library.BookDepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookDepartmentRepository extends JpaRepository<BookDepartmentEntity, Long> {

  Optional<BookDepartmentEntity> findById(Long id);
}
