package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class CtfFlagDto {

  private String content;
  @JsonProperty(access = Access.READ_ONLY)
  private Boolean isCorrect;
  @JsonProperty(access = Access.READ_ONLY)
  private LocalDateTime solvedTime;

  public static CtfFlagDto toDto(CtfFlagEntity flag) {
    return CtfFlagDto.builder()
        .content(flag.getContent())
        .isCorrect(flag.getIsCorrect())
        .solvedTime(flag.getSolvedTime())
        .build();
  }
}
