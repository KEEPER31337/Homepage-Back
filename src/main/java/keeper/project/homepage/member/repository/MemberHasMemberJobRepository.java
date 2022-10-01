package keeper.project.homepage.member.repository;

import java.util.List;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberHasMemberJobRepository extends
    JpaRepository<MemberHasMemberJobEntity, Long> {

  List<MemberHasMemberJobEntity> findAllByMemberEntity_Id(Long id);

  List<MemberHasMemberJobEntity> findAllByMemberEntity_IdAndAndMemberJobEntity_Id(Long memberId,
      Long jobId);

  List<MemberHasMemberJobEntity> findAllByMemberJobEntity(MemberJobEntity memberJobEntity);

  MemberHasMemberJobEntity findFirstByMemberJobEntityOrderByIdDesc(MemberJobEntity memberJobEntity);

  List<MemberHasMemberJobEntity> findByMemberJobEntityIn(List<MemberJobEntity> memberJobEntity);

  void deleteAllByMemberEntityAndMemberJobEntity(
      MemberEntity memberEntity, MemberJobEntity memberJobEntity);
}
