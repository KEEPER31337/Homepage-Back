package keeper.project.homepage.user.dto.clerk.request;

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
public class AttendanceCheckRequestDto {

  @NotNull
  private Long seminarId;
  @NotNull
  private Long memberId;
  @NotNull
  private String attendanceCode;
  @NotNull
  private LocalDateTime attendTime;
}
