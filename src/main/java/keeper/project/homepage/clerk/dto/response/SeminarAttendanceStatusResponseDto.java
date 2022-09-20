package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeminarAttendanceStatusResponseDto {

  @NonNull
  private Long id;

  @NonNull
  private String seminarAttendanceStatusType;

  public static SeminarAttendanceStatusResponseDto from(
      SeminarAttendanceStatusEntity status) {
    return SeminarAttendanceStatusResponseDto.builder()
        .id(status.getId())
        .seminarAttendanceStatusType(status.getType())
        .build();
  }
}
