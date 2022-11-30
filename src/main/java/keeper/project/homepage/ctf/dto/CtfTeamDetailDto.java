package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.dto.CommonMemberDto;
import keeper.project.homepage.member.entity.MemberEntity;
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
  List<CtfCommonChallengeDto> solvedChallengeList;

  public static CtfTeamDetailDto toDto(CtfTeamEntity team,
      List<CtfChallengeEntity> solvedChallengeList) {
    return CtfTeamDetailDto.builder()
        .id(team.getId())
        .name(team.getName())
        .description(team.getDescription())
        .registerTime(team.getRegisterTime())
        .creatorId(team.getCreator().getId())
        .score(team.getScore())
        .contestId(team.getCtfContestEntity().getId())
        .lastSolvedTime(team.getLastSolveTime())
        .teamMembers(
            team.getCtfTeamHasMemberEntityList().stream()
                .map(ctfTeamHasMember -> CommonMemberDto.toDto(ctfTeamHasMember.getMember()))
                .toList())
        .solvedChallengeList(
            solvedChallengeList.stream()
                .map(challenge -> CtfCommonChallengeDto.toDto(challenge, true, 0L))
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
        .lastSolveTime(LocalDateTime.now())
        .build();
  }
}
