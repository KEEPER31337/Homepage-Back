package keeper.project.homepage.repository.posting;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.posting.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  List<CategoryEntity> findAllByParentId(Long parentId);

  List<CategoryEntity> findAllByName(String name);
}
