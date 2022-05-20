package keeper.project.homepage.repository.about;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticWriteTitleRepository extends
    JpaRepository<StaticWriteTitleEntity, Long> {

  List<StaticWriteTitleEntity> findAllByType(String type);

}
