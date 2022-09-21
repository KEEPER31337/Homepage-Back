package keeper.project.homepage.about.repository;

import keeper.project.homepage.about.entity.StaticWriteContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticWriteContentRepository extends
    JpaRepository<StaticWriteContentEntity, Long> {

}
