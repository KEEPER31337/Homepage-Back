package keeper.project.homepage.admin.dto.clerk.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeritLogCreateRequestDto {

  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  @NotNull
  private Long memberId;

  @NotNull
  private Long meritTypeId;

}
