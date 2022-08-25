package keeper.project.homepage.admin.dto.clerk.response;

import keeper.project.homepage.entity.clerk.SurveyEntity;
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
public class SurveyUpdateResponseDto {

  private Long surveyId;
  private String surveyName;
  private Boolean isVisible;

  public static SurveyUpdateResponseDto from(SurveyEntity survey) {
    return SurveyUpdateResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .isVisible(survey.getIsVisible())
        .build();
  }
}
