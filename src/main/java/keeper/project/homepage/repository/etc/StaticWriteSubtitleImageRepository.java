package keeper.project.homepage.repository.etc;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticWriteSubtitleImageRepository extends
    JpaRepository<StaticWriteSubtitleImageEntity, Long> {

  List<StaticWriteSubtitleImageEntity> findAllByStaticWriteTitle_Type(String type);
}
