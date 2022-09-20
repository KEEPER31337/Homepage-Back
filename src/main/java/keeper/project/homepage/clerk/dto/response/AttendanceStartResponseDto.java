package keeper.project.homepage.clerk.dto.response;

import java.time.LocalDateTime;
import keeper.project.homepage.clerk.entity.SeminarEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceStartResponseDto {

  private LocalDateTime attendanceCloseTime;
  private LocalDateTime latenessCloseTime;
  private String attendanceCode;

  public static AttendanceStartResponseDto from(SeminarEntity seminar){
    return AttendanceStartResponseDto.builder()
        .attendanceCloseTime(seminar.getAttendanceCloseTime())
        .latenessCloseTime(seminar.getLatenessCloseTime())
        .attendanceCode(seminar.getAttendanceCode())
        .build();
  }

}
