package keeper.project.homepage.admin.dto.clerk.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeminarAttendanceUpdateRequestDto {

  @NotNull
  private Long seminarAttendanceStatusId;

  private String absenceExcuse;
}
