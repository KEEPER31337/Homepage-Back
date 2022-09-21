package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckResponseDto {

  public static final AttendanceCheckResponseDto IMPOSSIBLE = new AttendanceCheckResponseDto(false,
      "출석불가능");

  private Boolean isPossibleAttendance;
  private String attendanceStatus;

  public static AttendanceCheckResponseDto from(SeminarAttendanceStatusEntity status) {
    return AttendanceCheckResponseDto.builder()
        .isPossibleAttendance(true)
        .attendanceStatus(status.getType())
        .build();
  }

}
