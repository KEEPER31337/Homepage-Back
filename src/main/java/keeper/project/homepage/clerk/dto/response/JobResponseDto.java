package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.member.entity.MemberJobEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JobResponseDto {

  @NonNull
  private Long id;
  @NonNull
  private String name;
  @NonNull
  private String badgePath;

  public static JobResponseDto toDto(MemberJobEntity memberJobEntity) {
    return JobResponseDto.builder()
        .id(memberJobEntity.getId())
        .name(memberJobEntity.getName())
        .badgePath(memberJobEntity.getBadgePath())
        .build();
  }
}
