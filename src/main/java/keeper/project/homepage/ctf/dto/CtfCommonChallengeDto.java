package keeper.project.homepage.ctf.dto;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
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
public class CtfCommonChallengeDto {

  public static final long MAX_SUBMIT_COUNT = 50;
  public static final long MIN_SUBMIT_COUNT = 1;
  public static final long DEFAULT_SUBMIT_COUNT = 15;

  private String title;
  private Long score;
  private List<CtfChallengeCategoryDto> categories;
  private Long contestId;
  @Max(MAX_SUBMIT_COUNT)
  @Min(MIN_SUBMIT_COUNT)
  @JsonSetter(nulls = Nulls.SKIP)
  private Long maxSubmitCount = DEFAULT_SUBMIT_COUNT;

  @JsonProperty(access = Access.READ_ONLY)
  private Long remainedSubmitCount;
  @JsonProperty(access = Access.READ_ONLY)
  private LocalDateTime lastTryTime;
  @JsonProperty(access = Access.READ_ONLY)
  private LocalDateTime solvedTime;
  @JsonProperty(access = Access.READ_ONLY)
  private Long challengeId;
  @JsonProperty(access = Access.READ_ONLY)
  private Boolean isSolved;

  public static CtfCommonChallengeDto toDto(CtfChallengeEntity challenge, Boolean isSolved,
      CtfFlagEntity ctfFlagEntity) {

    return CtfCommonChallengeDto.builder()
        .challengeId(challenge.getId())
        .title(challenge.getName())
        .contestId(challenge.getCtfContestEntity().getId())
        .categories(challenge.getCtfChallengeHasCtfChallengeCategoryList()
            .stream()
            .map(CtfChallengeCategoryDto::toDto)
            .collect(toList()))
        .score(challenge.getScore())
        .isSolved(isSolved)
        .remainedSubmitCount(ctfFlagEntity.getRemainedSubmitCount())
        .lastTryTime(ctfFlagEntity.getLastTryTime())
        .solvedTime(ctfFlagEntity.getSolvedTime())
        .maxSubmitCount(challenge.getMaxSubmitCount())
        .build();
  }
}
