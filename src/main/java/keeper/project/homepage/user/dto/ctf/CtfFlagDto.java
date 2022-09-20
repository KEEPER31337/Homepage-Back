package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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

  public static CtfFlagDto toDto(CtfFlagEntity flag) {
    return CtfFlagDto.builder()
        .content(flag.getContent())
        .isCorrect(flag.getIsCorrect())
        .build();
  }
}
