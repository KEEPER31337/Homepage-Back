package keeper.project.homepage.repository.posting;

import java.util.List;
import keeper.project.homepage.entity.posting.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  CategoryEntity findByName(String name);
}
