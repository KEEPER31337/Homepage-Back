package keeper.project.homepage.repository.attendance;

import java.util.Optional;
import keeper.project.homepage.entity.attendance.GameEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

  Optional<GameEntity> findByMember(MemberEntity memberEntity);
}
