package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.clerk.entity.SeminarEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarOngoingAttendanceResponseDto {

  public static final SeminarOngoingAttendanceResponseDto NONE = new SeminarOngoingAttendanceResponseDto(
      -1L, "Not Exist Seminar", false);

  private Long seminarId;
  private String seminarName;
  private Boolean isExist;

  public static SeminarOngoingAttendanceResponseDto from(SeminarEntity seminar) {
    return SeminarOngoingAttendanceResponseDto.builder()
        .seminarId(seminar.getId())
        .seminarName(seminar.getName())
        .isExist(true)
        .build();
  }

}
