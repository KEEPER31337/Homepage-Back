package keeper.project.homepage.repository.ctf;

import java.util.List;
import keeper.project.homepage.ctf.entity.CtfTeamHasMemberEntity;
import keeper.project.homepage.ctf.entity.CtfTeamHasMemberEntityPK;
import keeper.project.homepage.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfTeamHasMemberRepository extends
    JpaRepository<CtfTeamHasMemberEntity, CtfTeamHasMemberEntityPK> {

  List<CtfTeamHasMemberEntity> findAllByMember(MemberEntity memberEntity);

  List<CtfTeamHasMemberEntity> findAllByMemberId(Long memberId);

  void deleteAllByTeamId(Long teamId);
}
