package keeper.project.homepage.user.dto.point.request;

import java.time.LocalDateTime;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.point.entity.PointLogEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointLogRequestDto {
  private LocalDateTime time;
  private Integer point;
  private String detail;

  public PointLogEntity toEntity(MemberEntity member, Integer isSpent) {
    return PointLogEntity.builder()
        .member(member)
        .time(time)
        .point(point)
        .detail(detail)
        .isSpent(isSpent)
        .build();
  }
}
