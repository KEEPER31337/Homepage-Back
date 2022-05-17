package keeper.project.homepage.admin.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfSubmitLogEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.ctf.CtfTeamDto;
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
public class CtfSubmitLogDto {

  @JsonProperty(access = Access.READ_ONLY)
  Long id;

  @JsonProperty(access = Access.READ_ONLY)
  LocalDateTime submitTime;

  @JsonProperty(access = Access.READ_ONLY)
  CtfTeamDto team;

  @JsonProperty(access = Access.READ_ONLY)
  CommonMemberDto submitter;

  @JsonProperty(access = Access.READ_ONLY)
  CtfChallengeAdminDto challenge;

  @JsonProperty(access = Access.READ_ONLY)
  String flagSubmitted;

  @JsonProperty(access = Access.READ_ONLY)
  Boolean isCorrect;

  public static CtfSubmitLogDto toDto(CtfSubmitLogEntity submitLog) {
    CtfTeamDto team = CtfTeamDto.toDto(submitLog.getCtfTeamEntity());
    CommonMemberDto submitter = CommonMemberDto.toDto(submitLog.getSubmitter());
    CtfChallengeAdminDto challenge = CtfChallengeAdminDto.toDto(submitLog.getCtfChallengeEntity());

    return CtfSubmitLogDto.builder()
        .id(submitLog.getId())
        .submitTime(submitLog.getSubmitTime())
        .team(team)
        .submitter(submitter)
        .challenge(challenge)
        .flagSubmitted(submitLog.getFlagSubmitted())
        .isCorrect(submitLog.getIsCorrect())
        .build();
  }
}
