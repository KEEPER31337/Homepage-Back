package keeper.project.homepage.repository.member;

import keeper.project.homepage.entity.member.MemberRankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRankRepository extends JpaRepository<MemberRankEntity, Long> {

}
