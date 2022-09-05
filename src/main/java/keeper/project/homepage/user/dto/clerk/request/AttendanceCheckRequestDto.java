package keeper.project.homepage.user.dto.clerk.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
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

  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime attendanceTime;

}
