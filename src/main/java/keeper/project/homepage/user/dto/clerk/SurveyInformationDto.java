package keeper.project.homepage.user.dto.clerk;

import java.time.LocalDateTime;
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
public class SurveyInformationDto {

  private Long surveyId;
  private String surveyName;
  private LocalDateTime openTime;
  private LocalDateTime closeTime;
  private Boolean isResponded;
  private Boolean isVisible;
  private String reply;

  public static SurveyInformationDto toDto(SurveyEntity survey, String reply, Boolean isResponded) {
    return SurveyInformationDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .openTime(survey.getOpenTime())
        .closeTime(survey.getCloseTime())
        .isVisible(survey.getIsVisible())
        .isResponded(isResponded)
        .reply(reply)
        .build();
  }
}
