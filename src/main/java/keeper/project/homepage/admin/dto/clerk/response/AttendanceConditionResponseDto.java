package keeper.project.homepage.admin.dto.clerk.response;

import java.time.LocalDateTime;
import keeper.project.homepage.admin.dto.clerk.request.AttendanceConditionRequestDto;
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
public class AttendanceConditionResponseDto {
  @NonNull
  private String attendanceCode;
  @NonNull
  private LocalDateTime attendanceCloseTime;
  @NonNull
  private LocalDateTime latenessCloseTime;

  public static AttendanceConditionResponseDto from(SeminarEntity seminar){
    return AttendanceConditionResponseDto.builder()
        .attendanceCode(seminar.getAttendanceCode())
        .attendanceCloseTime(seminar.getAttendanceCloseTime())
        .latenessCloseTime(seminar.getLatenessCloseTime())
        .build();
  }
}
