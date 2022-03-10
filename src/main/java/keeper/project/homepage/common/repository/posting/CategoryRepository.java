package keeper.project.homepage.common.repository.posting;

import java.util.List;
import keeper.project.homepage.common.entity.posting.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  List<CategoryEntity> findAllByParentId(Long parentId);

  List<CategoryEntity> findAllByParentIdIsNull();

  CategoryEntity findByName(String name);
}
