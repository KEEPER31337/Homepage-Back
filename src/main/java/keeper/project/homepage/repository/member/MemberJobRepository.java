package keeper.project.homepage.repository.member;

import java.util.Optional;
import keeper.project.homepage.entity.member.MemberJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJobRepository extends JpaRepository<MemberJobEntity, Long> {

  Optional<MemberJobEntity> findByName(String name);
}
