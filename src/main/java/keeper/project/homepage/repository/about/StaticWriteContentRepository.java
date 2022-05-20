package keeper.project.homepage.repository.about;

import keeper.project.homepage.entity.about.StaticWriteContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticWriteContentRepository extends
    JpaRepository<StaticWriteContentEntity, Long> {

}
