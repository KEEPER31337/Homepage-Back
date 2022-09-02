package keeper.project.homepage.admin.dto.clerk.response;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyListResponseDto {
  @NonNull
  private Long surveyId;
  @NonNull
  private String surveyName;
  @NonNull
  private LocalDateTime openTime;
  @NonNull
  private LocalDateTime closeTime;
  @NotNull
  private String description;
  @NonNull
  private Boolean isVisible;

  public static SurveyListResponseDto from(SurveyEntity survey){
    return SurveyListResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .openTime(survey.getOpenTime())
        .closeTime(survey.getCloseTime())
        .description(survey.getDescription())
        .isVisible(survey.getIsVisible())
        .build();
  }
}
