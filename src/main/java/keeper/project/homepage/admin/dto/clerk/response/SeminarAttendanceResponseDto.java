package keeper.project.homepage.admin.dto.clerk.response;

import java.util.Comparator;
import java.util.List;
import keeper.project.homepage.admin.dto.clerk.SeminarAttendanceDto;
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
public class SeminarAttendanceResponseDto {

  @NonNull
  private Long seminarId;

  @NonNull
  private String seminarName;

  List<SeminarAttendanceDto> sortedSeminarAttendances;

  public static SeminarAttendanceResponseDto from(SeminarEntity seminar) {
    return SeminarAttendanceResponseDto.builder()
        .seminarId(seminar.getId())
        .seminarName(seminar.getName())
        .sortedSeminarAttendances(
            seminar.getSeminarAttendanceEntity().stream()
                .map(SeminarAttendanceDto::toDto)
                .sorted(Comparator.comparing(SeminarAttendanceDto::getGeneration)
                    .thenComparing(SeminarAttendanceDto::getMemberName))
                .toList()
        )
        .build();
  }
}
