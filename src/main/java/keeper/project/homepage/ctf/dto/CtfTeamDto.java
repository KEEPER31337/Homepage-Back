package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(Include.NON_NULL)
public class CtfTeamDto {

  String name;
  String description;

  @JsonProperty(access = Access.READ_ONLY)
  Long id;
  @JsonProperty(access = Access.READ_ONLY)
  Long score;
  @JsonProperty(access = Access.READ_ONLY)
  LocalDateTime lastSolvedTime;

  public static CtfTeamDto toDto(CtfTeamEntity team) {
    return CtfTeamDto.builder()
        .id(team.getId())
        .name(team.getName())
        .description(team.getDescription())
        .score(team.getScore())
        .lastSolvedTime(team.getLastSolveTime())
        .build();
  }
}
