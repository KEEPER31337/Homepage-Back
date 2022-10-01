package keeper.project.homepage.clerk.dto.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import keeper.project.homepage.clerk.entity.MeritLogEntity;
import keeper.project.homepage.clerk.entity.MeritTypeEntity;
import keeper.project.homepage.member.entity.MemberEntity;
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
public class MemberTotalMeritLogsResponseDto {

  @NonNull
  private Long memberId;

  @NonNull
  private String realName;

  @NonNull
  private Integer totalMerit;

  @NonNull
  private Integer totalDemerit;

  @NonNull
  private Map<String, Long> detailsWithCount;

  public static MemberTotalMeritLogsResponseDto of(MemberEntity member,
      List<MeritLogEntity> meritLogs) {
    return MemberTotalMeritLogsResponseDto.builder()
        .memberId(member.getId())
        .realName(member.getRealName())
        .totalMerit(
            meritLogs.stream().map(MeritLogEntity::getMeritType)
                .filter(MeritTypeEntity::getIsMerit)
                .mapToInt(MeritTypeEntity::getMerit)
                .sum())
        .totalDemerit(
            meritLogs.stream().map(MeritLogEntity::getMeritType)
                .filter(type -> type.getIsMerit().equals(false))
                .mapToInt(MeritTypeEntity::getMerit)
                .sum())
        .detailsWithCount(
            meritLogs.stream()
                .map(MeritLogEntity::getMeritType)
                .collect(Collectors.groupingBy(MeritTypeEntity::getDetail, Collectors.counting())))
        .build();
  }
}
