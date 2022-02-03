package keeper.project.homepage.repository.member;

import java.util.List;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberHasMemberJobRepository extends
    JpaRepository<MemberHasMemberJobEntity, Long> {

  List<MemberHasMemberJobEntity> findAllByMemberEntity_Id(Long id);
}