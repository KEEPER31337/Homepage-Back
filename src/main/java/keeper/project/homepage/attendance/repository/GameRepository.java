package keeper.project.homepage.attendance.repository;

import java.util.Optional;
import keeper.project.homepage.attendance.entity.GameEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

  Optional<GameEntity> findByMember(MemberEntity memberEntity);

  void deleteByMember(MemberEntity member);
}
