package keeper.project.homepage.admin.dto.clerk.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarWithAttendancesRequestByPeriodDto {
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime seasonStartDate;

  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime seasonEndDate;
}
