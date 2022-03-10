package keeper.project.homepage.user.dto.point.result;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.point.PointLogEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointLogResult {

  private Long memberId;
  private LocalDateTime time;
  private Integer point;
  private String detail;
  private Integer isSpent;
  private Integer prePoint;
  private Integer finalPoint;

  public PointLogResult(PointLogEntity pointLogEntity, int prePoint, int finalPoint) {
    this.memberId = pointLogEntity.getMember().getId();
    this.time = pointLogEntity.getTime();
    this.point = pointLogEntity.getPoint();
    this.detail = pointLogEntity.getDetail();
    this.isSpent = pointLogEntity.getIsSpent();
    this.prePoint = prePoint;
    this.finalPoint = finalPoint;
  }

  public PointLogResult(PointLogEntity pointLogEntity) {
    this.memberId = pointLogEntity.getMember().getId();
    this.time = pointLogEntity.getTime();
    this.point = pointLogEntity.getPoint();
    this.detail = pointLogEntity.getDetail();
    this.isSpent = pointLogEntity.getIsSpent();
  }
}
