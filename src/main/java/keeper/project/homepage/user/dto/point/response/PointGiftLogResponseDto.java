package keeper.project.homepage.user.dto.point.response;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.point.PointLogEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointGiftLogResponseDto {

  private Long memberId;
  private LocalDateTime time;
  private Integer point;
  private String detail;
  private Long presentedMemberId;
  private Integer prePointMember;
  private Integer finalPointMember;
  private Integer prePointPresented;
  private Integer finalPointPresented;

  public PointGiftLogResponseDto(PointLogEntity pointLogEntity, int prePointMember,
      int prePointPresented, int finalPointMember, int finalPointPresented) {
    this.memberId = pointLogEntity.getMember().getId();
    this.time = pointLogEntity.getTime();
    this.point = pointLogEntity.getPoint();
    this.detail = pointLogEntity.getDetail();
    this.presentedMemberId = pointLogEntity.getPresentedMember().getId();
    this.prePointMember = prePointMember;
    this.prePointPresented = prePointPresented;
    this.finalPointMember = finalPointMember;
    this.finalPointPresented = finalPointPresented;
  }

}
