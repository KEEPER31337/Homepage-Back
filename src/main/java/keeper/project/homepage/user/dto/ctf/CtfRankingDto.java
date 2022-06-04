package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
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
public class CtfRankingDto extends CtfTeamDto {

  @JsonProperty(access = Access.READ_ONLY)
  Long rank;

  public static CtfRankingDto toDto(CtfTeamEntity team, Long rank) {
    return CtfRankingDto.builder()
        .id(team.getId())
        .name(team.getName())
        .description(team.getDescription())
        .score(team.getScore())
        .rank(rank)
        .build();
  }
}
