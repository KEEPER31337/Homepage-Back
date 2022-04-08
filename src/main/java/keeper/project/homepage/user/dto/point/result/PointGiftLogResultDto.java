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
public class PointGiftLogResultDto {

  private String memberName;
  private LocalDateTime time;
  private Integer point;
  private String detail;
  private String presentedMemberName;
  private Integer prePointMember;
  private Integer finalPointMember;
  private Integer prePointPresented;
  private Integer finalPointPresented;

  public PointGiftLogResultDto(PointLogEntity pointLogEntity, int prePointMember,
      int prePointPresented, int finalPointMember, int finalPointPresented) {
    this.memberName = pointLogEntity.getMember().getRealName();
    this.time = pointLogEntity.getTime();
    this.point = pointLogEntity.getPoint();
    this.detail = pointLogEntity.getDetail();
    this.presentedMemberName = pointLogEntity.getPresentedMember().getRealName();
    this.prePointMember = prePointMember;
    this.prePointPresented = prePointPresented;
    this.finalPointMember = finalPointMember;
    this.finalPointPresented = finalPointPresented;
  }

  public PointGiftLogResultDto(PointLogEntity pointLogEntity) {
    this.memberName = pointLogEntity.getMember().getRealName();
    this.time = pointLogEntity.getTime();
    this.point = pointLogEntity.getPoint();
    this.detail = pointLogEntity.getDetail();
    this.presentedMemberName = pointLogEntity.getPresentedMember().getRealName();
  }
}
