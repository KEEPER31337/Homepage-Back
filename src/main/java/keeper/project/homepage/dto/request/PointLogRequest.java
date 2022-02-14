package keeper.project.homepage.dto.request;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointLogRequest {

  private LocalDateTime time;

  private Integer point;

  private String detail;

  private Integer isSpent;

  public PointLogEntity toEntity(MemberEntity member) {
    return PointLogEntity.builder()
        .member(member)
        .time(time)
        .point(point)
        .detail(detail)
        .isSpent(isSpent)
        .build();
  }
}
