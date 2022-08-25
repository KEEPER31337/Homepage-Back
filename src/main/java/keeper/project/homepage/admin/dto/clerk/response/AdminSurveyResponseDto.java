package keeper.project.homepage.admin.dto.clerk.response;

import java.time.LocalDateTime;
import java.util.List;
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
public class AdminSurveyResponseDto {

  private Long surveyId;
  private String surveyName;
  private LocalDateTime openTime;
  private LocalDateTime closeTime;
  private String description;
  private Boolean isVisible;
  private List<SurveyRespondentResponseDto> respondents;

  public static AdminSurveyResponseDto toDto(SurveyEntity survey) {
    return AdminSurveyResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .openTime(survey.getOpenTime())
        .closeTime(survey.getCloseTime())
        .description(survey.getDescription())
        .isVisible(survey.getIsVisible())
        .respondents(
            survey.getRespondents().stream().map(SurveyRespondentResponseDto::toDto).toList())
        .build();
  }
}
