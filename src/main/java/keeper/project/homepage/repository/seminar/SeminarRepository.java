package keeper.project.homepage.repository.seminar;

import javax.validation.constraints.NotNull;
import keeper.project.homepage.entity.seminar.SeminarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarRepository extends JpaRepository<SeminarEntity, Long> {

  SeminarEntity findByName(@NotNull String name);
}
