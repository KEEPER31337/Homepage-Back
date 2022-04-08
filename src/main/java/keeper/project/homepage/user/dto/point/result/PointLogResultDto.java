package keeper.project.homepage.user.dto.point.result;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import keeper.project.homepage.entity.point.PointLogEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
public class PointLogResultDto {

  private Long memberId;
  private LocalDateTime time;
  private Integer point;
  private String detail;
  private Integer isSpent;
  private Integer prePoint;
  private Integer finalPoint;

  public PointLogResultDto(PointLogEntity pointLogEntity, int prePoint, int finalPoint) {
    this.memberId = pointLogEntity.getMember().getId();
    this.time = pointLogEntity.getTime();
    this.point = pointLogEntity.getPoint();
    this.detail = pointLogEntity.getDetail();
    this.isSpent = pointLogEntity.getIsSpent();
    this.prePoint = prePoint;
    this.finalPoint = finalPoint;
  }

  public PointLogResultDto(PointLogEntity pointLogEntity) {
    this.memberId = pointLogEntity.getMember().getId();
    this.time = pointLogEntity.getTime();
    this.point = pointLogEntity.getPoint();
    this.detail = pointLogEntity.getDetail();
    this.isSpent = pointLogEntity.getIsSpent();
  }
}
