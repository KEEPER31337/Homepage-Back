package keeper.project.homepage.clerk.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckRequestDto {

  @NotNull
  private Long seminarId;

  @NotNull
  private String attendanceCode;

}
