package keeper.project.homepage.admin.dto.clerk.request;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSurveyRequestDto {

  @NotNull
  private String surveyName;
  @NotNull
  private LocalDateTime openTime;
  @NotNull
  private LocalDateTime closeTime;
  @Nullable
  private String description;
  @NotNull
  private Boolean isVisible;

  public SurveyEntity toEntity() {
    return SurveyEntity.builder()
        .name(this.surveyName)
        .openTime(this.openTime)
        .closeTime(this.closeTime)
        .description(this.description)
        .isVisible(this.isVisible)
        .build();
  }
}
