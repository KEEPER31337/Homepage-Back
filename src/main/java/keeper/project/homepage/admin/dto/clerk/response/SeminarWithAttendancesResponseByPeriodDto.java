package keeper.project.homepage.admin.dto.clerk.response;

import java.util.Comparator;
import java.util.List;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeminarWithAttendancesResponseByPeriodDto {

  @NonNull
  private Long seminarId;

  @NonNull
  private String seminarName;

  List<SeminarAttendanceResponseDto> sortedSeminarAttendances;

  public static SeminarWithAttendancesResponseByPeriodDto from(SeminarEntity seminar) {
    return SeminarWithAttendancesResponseByPeriodDto.builder()
        .seminarId(seminar.getId())
        .seminarName(seminar.getName())
        .sortedSeminarAttendances(
            seminar.getSeminarAttendances().stream()
                .map(SeminarAttendanceResponseDto::from)
                .sorted(Comparator.comparing(SeminarAttendanceResponseDto::getGeneration)
                    .thenComparing(SeminarAttendanceResponseDto::getMemberName))
                .toList()
        )
        .build();
  }
}
