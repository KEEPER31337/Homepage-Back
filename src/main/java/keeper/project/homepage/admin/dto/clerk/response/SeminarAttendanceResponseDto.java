package keeper.project.homepage.admin.dto.clerk.response;

import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarAttendanceResponseDto {

  @NonNull
  private Long attendanceId;

  @NonNull
  private String memberName;

  public static SeminarAttendanceResponseDto from(SeminarAttendanceEntity seminarAttendance) {
    return SeminarAttendanceResponseDto.builder()
        .attendanceId(seminarAttendance.getId())
        .memberName(seminarAttendance.getMemberEntity().getRealName())
        .build();
  }
}
