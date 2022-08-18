package keeper.project.homepage.repository.clerk;

import keeper.project.homepage.entity.clerk.SeminarEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarRepository extends JpaRepository<SeminarEntity, Long> {

  SeminarEntity findByNameIsLike(@NonNull String name);
}
