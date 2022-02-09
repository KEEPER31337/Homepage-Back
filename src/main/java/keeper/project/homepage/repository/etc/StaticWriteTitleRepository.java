package keeper.project.homepage.repository.etc;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticWriteTitleRepository extends
    JpaRepository<StaticWriteTitleEntity, Long> {

  Optional<StaticWriteTitleEntity> findByType(String type);

}
