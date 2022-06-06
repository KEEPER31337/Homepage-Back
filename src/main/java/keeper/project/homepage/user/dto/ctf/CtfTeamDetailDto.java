package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
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
public class CtfTeamDetailDto extends CtfTeamDto {

  @JsonProperty(access = Access.READ_ONLY)
  LocalDateTime registerTime;
  @JsonProperty(access = Access.READ_ONLY)
  Long creatorId;
  Long contestId;
  List<CommonMemberDto> teamMembers;

  public static CtfTeamDetailDto toDto(CtfTeamEntity team) {
    return CtfTeamDetailDto.builder()
        .id(team.getId())
        .name(team.getName())
        .description(team.getDescription())
        .registerTime(team.getRegisterTime())
        .creatorId(team.getCreator().getId())
        .score(team.getScore())
        .contestId(team.getCtfContestEntity().getId())
        .teamMembers(
            team.getCtfTeamHasMemberEntityList().stream()
                .map(ctfTeamHasMember -> CommonMemberDto.toDto(ctfTeamHasMember.getMember()))
                .toList())
        .build();
  }

  public CtfTeamEntity toEntity(CtfContestEntity contest, MemberEntity creator) {
    return CtfTeamEntity.builder()
        .name(name)
        .description(description)
        .registerTime(registerTime)
        .creator(creator)
        .score(0L)
        .ctfContestEntity(contest)
        .build();
  }
}
