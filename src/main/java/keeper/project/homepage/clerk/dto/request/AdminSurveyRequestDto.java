package keeper.project.homepage.clerk.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
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
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime openTime;
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
