package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.clerk.entity.SeminarEntity;
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

  public static SeminarCreateResponseDto from(SeminarEntity seminarEntity) {
    return SeminarCreateResponseDto.builder()
        .id(seminarEntity.getId())
        .build();
  }
}
