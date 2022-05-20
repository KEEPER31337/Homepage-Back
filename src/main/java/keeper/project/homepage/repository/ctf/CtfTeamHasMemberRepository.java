package keeper.project.homepage.repository.ctf;

import java.util.Optional;
import keeper.project.homepage.entity.ctf.CtfTeamHasMemberEntity;
import keeper.project.homepage.entity.ctf.CtfTeamHasMemberEntityPK;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfTeamHasMemberRepository extends
    JpaRepository<CtfTeamHasMemberEntity, CtfTeamHasMemberEntityPK> {

  Optional<CtfTeamHasMemberEntity> findByMember_Id(Long member_id);
}
