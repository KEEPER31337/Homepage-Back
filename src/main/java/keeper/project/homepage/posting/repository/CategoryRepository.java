package keeper.project.homepage.posting.repository;

import keeper.project.homepage.posting.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  CategoryEntity findByName(String name);
}
