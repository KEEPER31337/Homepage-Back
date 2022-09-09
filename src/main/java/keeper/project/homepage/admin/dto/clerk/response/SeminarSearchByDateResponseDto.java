package keeper.project.homepage.admin.dto.clerk.response;

import java.time.LocalDateTime;
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

  public static final SeminarSearchByDateResponseDto NONE = new SeminarSearchByDateResponseDto(false, -1L,
      "Not Exist Seminar", null, null, null);

  private Boolean isExist;
  private Long seminarId;
  private String seminarName;
  private LocalDateTime attendanceCloseTime;
  private LocalDateTime latenessCloseTime;
  private String attendanceCode;

  public static SeminarSearchByDateResponseDto from(SeminarEntity seminar) {
    return SeminarSearchByDateResponseDto.builder()
        .isExist(true)
        .seminarId(seminar.getId())
        .seminarName(seminar.getName())
        .attendanceCloseTime(seminar.getAttendanceCloseTime())
        .latenessCloseTime(seminar.getLatenessCloseTime())
        .attendanceCode(seminar.getAttendanceCode())
        .build();
  }
}
