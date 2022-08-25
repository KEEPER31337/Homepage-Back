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
public class DeleteSurveyResponseDto {
  private Long surveyId;
  private String surveyName;

  public static DeleteSurveyResponseDto toDto(SurveyEntity survey){
    return DeleteSurveyResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .build();
  }
}
