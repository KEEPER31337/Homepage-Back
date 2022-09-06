package keeper.project.homepage.admin.dto.clerk.response;

import java.util.List;
import keeper.project.homepage.entity.clerk.MeritLogEntity;
import keeper.project.homepage.entity.clerk.MeritTypeEntity;
import keeper.project.homepage.entity.member.MemberEntity;
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
  private List<String> details;

  public static MemberTotalMeritLogsResponseDto of(MemberEntity member, List<MeritLogEntity> meritLogs) {
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
        .details(
            meritLogs.stream()
                .map(MeritLogEntity::getMeritType)
                .map(MeritTypeEntity::getDetail)
                .toList())
        .build();
  }
}
