package keeper.project.homepage.admin.dto.clerk;

import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarAttendanceDto {

  @NonNull
  private Float generation;

  @NonNull
  private String memberName;

  @NonNull
  private String seminarAttendanceStatusType;

  private String absenceExcuse;

  public static SeminarAttendanceDto toDto(SeminarAttendanceEntity seminarAttendanceEntity) {
    SeminarAttendanceExcuseEntity attendanceExcuse = seminarAttendanceEntity.getSeminarAttendanceExcuseEntity();
    String absenceExcuse = attendanceExcuse == null ? null : attendanceExcuse.getAbsenceExcuse();

    return SeminarAttendanceDto.builder()
        .generation(seminarAttendanceEntity.getMemberEntity().getGeneration())
        .memberName(seminarAttendanceEntity.getMemberEntity().getRealName())
        .seminarAttendanceStatusType(
            seminarAttendanceEntity.getSeminarAttendanceStatusEntity().getType())
        .absenceExcuse(absenceExcuse)
        .build();
  }
}
