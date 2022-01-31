package keeper.project.homepage.repository.etc;

import keeper.project.homepage.entity.etc.StaticWriteContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticWriteContentRepository extends
    JpaRepository<StaticWriteContentEntity, Long> {

}
