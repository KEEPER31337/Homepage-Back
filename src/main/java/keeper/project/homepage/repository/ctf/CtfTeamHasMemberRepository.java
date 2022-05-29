package keeper.project.homepage.repository.ctf;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.ctf.CtfTeamHasMemberEntity;
import keeper.project.homepage.entity.ctf.CtfTeamHasMemberEntityPK;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfTeamHasMemberRepository extends
    JpaRepository<CtfTeamHasMemberEntity, CtfTeamHasMemberEntityPK> {

  List<CtfTeamHasMemberEntity> findAllByMember(MemberEntity memberEntity);

  List<CtfTeamHasMemberEntity> findAllByMemberId(Long memberId);

  void deleteAllByTeamId(Long teamId);
}
