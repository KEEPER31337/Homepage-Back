package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import keeper.project.homepage.admin.dto.ctf.CtfContestDto;
import keeper.project.homepage.admin.dto.ctf.CtfSubmitLogDto;
import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfSubmitLogEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
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
  CommonMemberDto creator;
  @JsonProperty(access = Access.READ_ONLY)
  Long score;
  CtfContestDto contest;

  public static CtfTeamDto toDto(CtfTeamEntity team) {
    CommonMemberDto creator = CommonMemberDto.toDto(team.getCreator());
    CtfContestDto contest = CtfContestDto.toDto((team.getCtfContestEntity()));
    return CtfTeamDto.builder()
        .id(team.getId())
        .name(team.getName())
        .description(team.getDescription())
        .registerTime(team.getRegisterTime())
        .creator(creator)
        .score(team.getScore())
        .contest(contest)
        .build();
  }
}
