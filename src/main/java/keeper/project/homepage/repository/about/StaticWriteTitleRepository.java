package keeper.project.homepage.repository.about;

import java.util.List;
import keeper.project.homepage.about.entity.StaticWriteTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StaticWriteTitleRepository extends
    JpaRepository<StaticWriteTitleEntity, Long> {

  List<StaticWriteTitleEntity> findAllByType(String type);

  @Query("select distinct s.type from StaticWriteTitleEntity s")
  List<String> getAllDistinctTypes();

}
