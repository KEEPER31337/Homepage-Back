package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.clerk.entity.SeminarAttendanceEntity;
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
