package keeper.project.homepage.admin.dto.clerk.request;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceConditionRequestDto {

  @NotNull
  private Long memberId;
  @NotNull
  private Long seminarId;
  @NotNull
  private LocalDateTime attendanceCloseTime;
  @NotNull
  private LocalDateTime latenessCloseTime;
}
