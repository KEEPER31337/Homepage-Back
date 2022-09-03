package keeper.project.homepage.admin.dto.clerk.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeritLogUpdateRequestDto {

  @NonNull
  private Long meritLogId;

  @NonNull
  private Long meritTypeId;

  @NonNull
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

}
