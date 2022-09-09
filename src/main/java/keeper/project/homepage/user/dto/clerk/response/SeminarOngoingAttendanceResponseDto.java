package keeper.project.homepage.user.dto.clerk.response;

import keeper.project.homepage.entity.clerk.SeminarEntity;
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
