package keeper.project.homepage.repository.clerk;

import javax.validation.constraints.NotNull;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarRepository extends JpaRepository<SeminarEntity, Long> {

  SeminarEntity findByName(@NotNull String name);
}
