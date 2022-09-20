package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.clerk.entity.SeminarAttendanceEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceExcuseEntity;
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
  private Long memberId;

  @NonNull
  private Float generation;

  @NonNull
  private String memberName;

  @NonNull
  private String attendanceStatusType;

  private String absenceExcuse;

  public static SeminarAttendanceResponseDto from(SeminarAttendanceEntity seminarAttendance) {
    SeminarAttendanceExcuseEntity attendanceExcuse = seminarAttendance.getSeminarAttendanceExcuseEntity();
    String absenceExcuse = attendanceExcuse == null ? null : attendanceExcuse.getAbsenceExcuse();

    return SeminarAttendanceResponseDto.builder()
        .attendanceId(seminarAttendance.getId())
        .memberId(seminarAttendance.getMemberEntity().getId())
        .generation(seminarAttendance.getMemberEntity().getGeneration())
        .memberName(seminarAttendance.getMemberEntity().getRealName())
        .attendanceStatusType(
            seminarAttendance.getSeminarAttendanceStatusEntity().getType())
        .absenceExcuse(absenceExcuse)
        .build();
  }
}
