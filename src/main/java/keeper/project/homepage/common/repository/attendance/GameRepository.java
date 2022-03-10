package keeper.project.homepage.common.repository.attendance;

import java.util.Optional;
import keeper.project.homepage.common.entity.attendance.GameEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

  Optional<GameEntity> findByMember(MemberEntity memberEntity);

  void deleteByMember(MemberEntity member);
}
