package keeper.project.homepage.user.dto.point.request;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointGiftLogRequestDto {

  private LocalDateTime time;

  private Integer point;

  private String detail;

  private Long presentedId;

  public PointLogEntity toEntity(MemberEntity member, MemberEntity presented, Integer isSpent) {
    String detailValue = detail;
    if (detailValue == null || detailValue.trim().isEmpty()) {
      if (isSpent == 0) {
        detailValue = "선물받은 포인트";
      } else if (isSpent == 1) {
        detailValue = "선물한 포인트";
      }
    }

    return PointLogEntity.builder()
        .member(member)
        .time(time)
        .point(point)
        .detail(detailValue)
        .presentedMember(presented)
        .isSpent(isSpent)
        .build();
  }

}
