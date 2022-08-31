package keeper.project.homepage.repository.clerk;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarRepository extends JpaRepository<SeminarEntity, Long> {

  List<SeminarEntity> findAllByOrderByOpenTimeDesc();

  Optional<SeminarEntity> findTop1ByOrderByIdDesc();

}
