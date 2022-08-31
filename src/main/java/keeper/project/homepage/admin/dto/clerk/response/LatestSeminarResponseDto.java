package keeper.project.homepage.admin.dto.clerk.response;

import keeper.project.homepage.entity.clerk.SeminarEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestSeminarResponseDto {

  @NonNull
  private Long seminarId;
  @NonNull
  private String seminarName;

  public static LatestSeminarResponseDto from(SeminarEntity seminar) {
    return LatestSeminarResponseDto.builder()
        .seminarId(seminar.getId())
        .seminarName(seminar.getName())
        .build();
  }
}
