package keeper.project.homepage.ctf.dto;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.util.dto.FileDto;
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
public class CtfChallengeDto extends CtfCommonChallengeDto {

  protected String content;

  @JsonProperty(access = Access.READ_ONLY)
  protected String creatorName;
  @JsonProperty(access = Access.READ_ONLY)
  protected Long solvedTeamCount;
  @JsonProperty(access = Access.READ_ONLY)
  @JsonInclude
  protected FileDto file;

  public static CtfChallengeDto toDto(CtfChallengeEntity challenge, Long solvedTeamCount,
      Boolean isSolved, CtfFlagEntity ctfFlagEntity) {
    FileDto file = FileDto.toDto(challenge.getFileEntity());

    return CtfChallengeDto.builder()
        .challengeId(challenge.getId())
        .title(challenge.getName())
        .content(challenge.getDescription())
        .contestId(challenge.getCtfContestEntity().getId())
        .category(challenge.getCtfChallengeHasCtfChallengeCategoryList()
            .stream()
            .map(CtfChallengeCategoryDto::toDto)
            .collect(toList()))
        .creatorName(challenge.getCreator().getNickName())
        .score(challenge.getScore())
        .solvedTeamCount(solvedTeamCount)
        .isSolved(isSolved)
        .file(file)
        .remainedSubmitCount(ctfFlagEntity.getRemainedSubmitCount())
        .lastTryTime(ctfFlagEntity.getLastTryTime())
        .solvedTime(ctfFlagEntity.getSolvedTime())
        .maxSubmitCount(challenge.getMaxSubmitCount())
        .build();
  }
}
