package keeper.project.homepage.user.dto.clerk.response;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyInformationResponseDto {

  @NonNull
  private Long surveyId;
  @NonNull
  private String surveyName;
  @NonNull
  private LocalDateTime openTime;
  @NonNull
  private LocalDateTime closeTime;
  @NonNull
  private Boolean isResponded;
  @NonNull
  private Boolean isVisible;
  @Nullable
  private String reply;

  public static SurveyInformationResponseDto toDto(SurveyEntity survey, String reply,
      Boolean isResponded) {
    return SurveyInformationResponseDto.builder()
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
