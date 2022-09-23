package keeper.project.homepage.clerk.repository;

import java.util.Optional;
import keeper.project.homepage.clerk.entity.MeritTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeritTypeRepository extends JpaRepository<MeritTypeEntity, Long> {
  Optional<MeritTypeEntity> findByDetail(String detail);
}
