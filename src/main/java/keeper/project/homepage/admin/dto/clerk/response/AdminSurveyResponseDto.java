package keeper.project.homepage.admin.dto.clerk.response;

import java.time.LocalDateTime;
import java.util.List;
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
public class AdminSurveyResponseDto {

  @NonNull
  private Long surveyId;
  @NonNull
  private String surveyName;
  @NonNull
  private LocalDateTime openTime;
  @NonNull
  private LocalDateTime closeTime;
  @Nullable
  private String description;
  @NonNull
  private Boolean isVisible;
  @Nullable
  private List<SurveyRespondentResponseDto> respondents;

  public static AdminSurveyResponseDto toDto(SurveyEntity survey) {
    return AdminSurveyResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .openTime(survey.getOpenTime())
        .closeTime(survey.getCloseTime())
        .description(survey.getDescription())
        .isVisible(survey.getIsVisible())
        .respondents(survey.getRespondents()
            .stream()
            .map(SurveyRespondentResponseDto::toDto)
            .toList())
        .build();
  }
}
