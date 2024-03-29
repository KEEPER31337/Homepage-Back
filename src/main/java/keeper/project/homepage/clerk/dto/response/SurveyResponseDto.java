package keeper.project.homepage.clerk.dto.response;

import java.time.LocalDateTime;
import keeper.project.homepage.clerk.entity.SurveyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseDto {

  @NonNull
  private Long surveyId;
  @NonNull
  private String surveyName;
  @NonNull
  private LocalDateTime openTime;
  @NonNull
  private LocalDateTime closeTime;
  @NonNull
  private String description;
  @NonNull
  private Boolean isVisible;

  public static SurveyResponseDto from(SurveyEntity survey) {
    return SurveyResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .openTime(survey.getOpenTime())
        .closeTime(survey.getCloseTime())
        .description(survey.getDescription())
        .isVisible(survey.getIsVisible())
        .build();
  }
}
