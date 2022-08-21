package keeper.project.homepage.admin.dto.clerk.response;

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
public class TypeResponseDto {

  @NonNull
  private Long id;
  @NonNull
  private String name;
  @NonNull
  private String badgePath;

  public static TypeResponseDto toDto(MemberTypeEntity memberTypeEntity) {
    return TypeResponseDto.builder()
        .id(memberTypeEntity.getId())
        .name(memberTypeEntity.getName())
        .badgePath(memberTypeEntity.getBadgePath())
        .build();
  }
}
