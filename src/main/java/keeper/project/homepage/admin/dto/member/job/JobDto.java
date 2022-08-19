package keeper.project.homepage.admin.dto.member.job;

import keeper.project.homepage.entity.member.MemberJobEntity;
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
public class JobDto {

  @NonNull
  private Long id;
  @NonNull
  private String name;

  public static JobDto toDto(MemberJobEntity memberJobEntity) {
    return JobDto.builder()
        .id(memberJobEntity.getId())
        .name(memberJobEntity.getName())
        .build();
  }
}
