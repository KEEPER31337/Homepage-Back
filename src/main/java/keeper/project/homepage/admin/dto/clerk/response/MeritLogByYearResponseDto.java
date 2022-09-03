package keeper.project.homepage.admin.dto.clerk.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import keeper.project.homepage.entity.clerk.MeritLogEntity;
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
  private Long memberId;

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
        .memberId(meritLog.getAwarder().getId())
        .date(meritLog.getDate())
        .isMerit(meritLog.getMeritType().getIsMerit())
        .merit(meritLog.getMeritType().getMerit())
        .detail(meritLog.getMeritType().getDetail())
        .build();
  }
}
