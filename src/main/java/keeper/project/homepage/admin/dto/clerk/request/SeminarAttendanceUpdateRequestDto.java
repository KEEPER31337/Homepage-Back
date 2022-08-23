package keeper.project.homepage.admin.dto.clerk.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeminarAttendanceUpdateRequestDto {

  @NonNull
  private Long seminarAttendanceStatusId;

  private String absenceExcuse;
}
