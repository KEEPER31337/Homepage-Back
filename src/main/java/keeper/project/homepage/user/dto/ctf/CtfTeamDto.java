package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
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
public class CtfTeamDto {

  @JsonProperty(access = Access.READ_ONLY)
  Long id;
  String name;
  String description;
  @JsonProperty(access = Access.READ_ONLY)
  LocalDateTime registerTime;
  @JsonProperty(access = Access.READ_ONLY)
  Long creatorId;
  @JsonProperty(access = Access.READ_ONLY)
  Long score;
  Long contestId;

  public static CtfTeamDto toDto(CtfTeamEntity team) {
    return CtfTeamDto.builder()
        .id(team.getId())
        .name(team.getName())
        .description(team.getDescription())
        .registerTime(team.getRegisterTime())
        .creatorId(team.getCreator().getId())
        .score(team.getScore())
        .contestId(team.getCtfContestEntity().getId())
        .build();
  }
}
