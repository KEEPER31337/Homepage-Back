package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.entity.clerk.SurveyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyUpdateResponseDto {

  @NonNull
  private Long surveyId;
  @NonNull
  private String surveyName;
  @NonNull
  private Boolean isVisible;

  public static SurveyUpdateResponseDto from(SurveyEntity survey) {
    return SurveyUpdateResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .isVisible(survey.getIsVisible())
        .build();
  }
}
