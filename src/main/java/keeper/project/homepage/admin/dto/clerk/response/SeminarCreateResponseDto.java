package keeper.project.homepage.admin.dto.clerk.response;

import keeper.project.homepage.entity.clerk.SeminarEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeminarCreateResponseDto {

  @NonNull
  private Long id;

  public static SeminarCreateResponseDto toDto(SeminarEntity seminarEntity) {
    return SeminarCreateResponseDto.builder()
        .id(seminarEntity.getId())
        .build();
  }
}
