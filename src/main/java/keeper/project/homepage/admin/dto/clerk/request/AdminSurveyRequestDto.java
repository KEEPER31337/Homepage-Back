package keeper.project.homepage.admin.dto.clerk.request;

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
public class AdminSurveyRequestDto {
  private String surveyName;
  private LocalDateTime openTime;
  private LocalDateTime closeTime;

  private String description;
  private Boolean isVisible;

  public SurveyEntity toEntity(){
    return SurveyEntity.builder()
        .name(this.surveyName)
        .openTime(this.openTime)
        .closeTime(this.closeTime)
        .description(this.description)
        .isVisible(this.isVisible)
        .build();
  }
}
