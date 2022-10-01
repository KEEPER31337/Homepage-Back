package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.clerk.entity.SurveyEntity;
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
public class DeleteSurveyResponseDto {

  @NonNull
  private Long surveyId;
  @NonNull
  private String surveyName;

  public static DeleteSurveyResponseDto from(SurveyEntity survey) {
    return DeleteSurveyResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .build();
  }
}
