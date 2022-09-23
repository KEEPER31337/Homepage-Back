package keeper.project.homepage.library.repository;

import java.util.Optional;
import keeper.project.homepage.library.entity.BookDepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookDepartmentRepository extends JpaRepository<BookDepartmentEntity, Long> {

  Optional<BookDepartmentEntity> findById(Long id);
}
