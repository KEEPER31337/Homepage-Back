package keeper.project.homepage.clerk.dto.response;

import java.time.LocalDate;
import keeper.project.homepage.clerk.entity.MeritLogEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MeritLogByYearResponseDto {

  @NonNull
  private Long meritLogId;

  @NonNull
  private String awarderRealName;

  @NonNull
  private LocalDate date;

  @NonNull
  private Boolean isMerit;

  @NonNull
  private Integer merit;

  @NonNull
  private String detail;

  public static MeritLogByYearResponseDto from(MeritLogEntity meritLog) {
    return MeritLogByYearResponseDto.builder()
        .meritLogId(meritLog.getId())
        .awarderRealName(meritLog.getAwarder().getRealName())
        .date(meritLog.getDate())
        .isMerit(meritLog.getMeritType().getIsMerit())
        .merit(meritLog.getMeritType().getMerit())
        .detail(meritLog.getMeritType().getDetail())
        .build();
  }
}
