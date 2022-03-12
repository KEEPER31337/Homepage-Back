package keeper.project.homepage.user.dto.point.request;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointGiftLogRequest {

  private LocalDateTime time;

  private Integer point;

  private String detail;

  private Long presentedId;

  public PointLogEntity toEntity(MemberEntity member, MemberEntity presented) {
    return PointLogEntity.builder()
        .member(member)
        .time(time)
        .point(point)
        .detail(detail)
        .presentedMember(presented)
        .isSpent(1)
        .build();
  }

}
