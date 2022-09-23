package keeper.project.homepage.clerk.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeritTypeCreateRequestDto {

  @NotNull
  private Integer merit;

  @NotNull
  private Boolean isMerit;

  @NotNull
  private String detail;
}
