package keeper.project.homepage.user.dto.clerk.request;

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
public class SurveyInformationRequestDto {

  @NotNull
  private Long surveyId;
  @NotNull
  private Long memberId;
}
