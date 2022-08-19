package keeper.project.homepage.admin.dto.member.type;

import keeper.project.homepage.admin.dto.member.MemberTypeDto;
import keeper.project.homepage.admin.dto.member.job.JobDto;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
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
public class TypeDto {

  @NonNull
  private Long id;

  @NonNull
  private String name;

  public static TypeDto toDto(MemberTypeEntity memberTypeEntity) {
    return TypeDto.builder()
        .id(memberTypeEntity.getId())
        .name(memberTypeEntity.getName())
        .build();
  }
}
