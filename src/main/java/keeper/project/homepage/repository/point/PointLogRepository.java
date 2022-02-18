package keeper.project.homepage.repository.point;

import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointLogRepository extends JpaRepository<PointLogEntity, Long> {

  List<PointLogEntity> findAllByMemberAndPresentedMemberIsNull(MemberEntity memberEntity, Pageable pageable);

  List<PointLogEntity> findAllByMemberAndPresentedMemberIsNotNull(MemberEntity memberEntity, Pageable pageable);

  List<PointLogEntity> findAllByPresentedMemberAndMemberIsNotNull(MemberEntity memberEntity, Pageable pageable);

}
