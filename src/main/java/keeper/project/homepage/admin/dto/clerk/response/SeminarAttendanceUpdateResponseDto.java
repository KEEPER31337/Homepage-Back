package keeper.project.homepage.admin.dto.clerk.response;

import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeminarAttendanceUpdateResponseDto {

  @NonNull
  private String seminarAttendanceStatusType;

  public static SeminarAttendanceUpdateResponseDto from(
      SeminarAttendanceEntity seminarAttendance) {
    return SeminarAttendanceUpdateResponseDto.builder().seminarAttendanceStatusType(
            seminarAttendance.getSeminarAttendanceStatusEntity().getType())
        .build();
  }
}
