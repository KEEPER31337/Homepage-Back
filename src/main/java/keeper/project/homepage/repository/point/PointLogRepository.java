package keeper.project.homepage.repository.point;

import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointLogRepository extends JpaRepository<PointLogEntity, Long> {

  void deleteByMember(MemberEntity member);

  Page<PointLogEntity> findAllByMember(MemberEntity member, Pageable pageable);
}
