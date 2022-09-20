package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import keeper.project.homepage.entity.ctf.CtfSubmitLogEntity;
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
  String flagSubmitted;

  @JsonProperty(access = Access.READ_ONLY)
  Boolean isCorrect;

  @JsonProperty(access = Access.READ_ONLY)
  String teamName;

  @JsonProperty(access = Access.READ_ONLY)
  String submitterLoginId;

  @JsonProperty(access = Access.READ_ONLY)
  String submitterRealname;

  @JsonProperty(access = Access.READ_ONLY)
  String challengeName;

  @JsonProperty(access = Access.READ_ONLY)
  String contestName;

  @JsonProperty(access = Access.READ_ONLY)
  Long contestId;

  public static CtfSubmitLogDto toDto(CtfSubmitLogEntity submitLog) {

    return CtfSubmitLogDto.builder()
        .id(submitLog.getId())
        .submitTime(submitLog.getSubmitTime())
        .flagSubmitted(submitLog.getFlagSubmitted())
        .isCorrect(submitLog.getIsCorrect())
        .teamName(submitLog.getTeamName())
        .submitterLoginId(submitLog.getSubmitterLoginId())
        .submitterRealname(submitLog.getSubmitterRealname())
        .challengeName(submitLog.getChallengeName())
        .contestName(submitLog.getContestName())
        .contestId(submitLog.getContest().getId())
        .build();
  }
}
