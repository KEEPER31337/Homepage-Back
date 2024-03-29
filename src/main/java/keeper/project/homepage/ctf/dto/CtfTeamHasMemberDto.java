package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.ctf.entity.CtfTeamHasMemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CtfTeamHasMemberDto {

  @JsonProperty(access = Access.READ_ONLY)
  private String teamName;
  @JsonProperty(access = Access.READ_ONLY)
  private String memberNickname;

  public static CtfTeamHasMemberDto toDto(CtfTeamHasMemberEntity teamHasMemberEntity) {
    return CtfTeamHasMemberDto.builder()
        .teamName(teamHasMemberEntity.getTeam().getName())
        .memberNickname(teamHasMemberEntity.getMember().getNickName())
        .build();
  }
}
