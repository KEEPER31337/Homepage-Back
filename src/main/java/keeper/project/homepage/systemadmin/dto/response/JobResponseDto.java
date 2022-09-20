package keeper.project.homepage.systemadmin.dto.response;

import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
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
