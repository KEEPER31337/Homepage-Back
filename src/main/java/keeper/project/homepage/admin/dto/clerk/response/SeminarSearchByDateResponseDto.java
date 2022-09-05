package keeper.project.homepage.admin.dto.clerk.response;

import keeper.project.homepage.entity.clerk.SeminarEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarSearchByDateResponseDto {

  public static final SeminarSearchByDateResponseDto NONE = new SeminarSearchByDateResponseDto(-1L,
      "Not Exist Seminar", false);

  private Long seminarId;
  private String seminarName;
  private Boolean isExist;

  public static SeminarSearchByDateResponseDto from(SeminarEntity seminar) {
    return SeminarSearchByDateResponseDto.builder()
        .seminarId(seminar.getId())
        .seminarName(seminar.getName())
        .isExist(true)
        .build();
  }
}
