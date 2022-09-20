package keeper.project.homepage.repository.member;

import java.util.Optional;
import keeper.project.homepage.member.entity.MemberRankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRankRepository extends JpaRepository<MemberRankEntity, Long> {

  Optional<MemberRankEntity> findByName(String name);
}
