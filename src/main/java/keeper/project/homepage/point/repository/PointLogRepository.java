package keeper.project.homepage.point.repository;

import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.point.entity.PointLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointLogRepository extends JpaRepository<PointLogEntity, Long> {

  void deleteByMember(MemberEntity member);

  Page<PointLogEntity> findAllByMember(MemberEntity member, Pageable pageable);
}
