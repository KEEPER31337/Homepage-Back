package keeper.project.homepage.user.dto.clerk;

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
  private Long surveyId;
  private Long memberId;
}
