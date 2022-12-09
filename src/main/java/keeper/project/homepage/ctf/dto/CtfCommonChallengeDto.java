package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.time.LocalDateTime;
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

  protected String title;
  protected Long score;
  protected CtfChallengeCategoryDto category;
  protected Long contestId;
  @Max(MAX_SUBMIT_COUNT)
  @Min(MIN_SUBMIT_COUNT)
  @JsonSetter(nulls = Nulls.SKIP)
  protected Long maxSubmitCount = DEFAULT_SUBMIT_COUNT;

  @JsonProperty(access = Access.READ_ONLY)
  protected Long remainedSubmitCount;
  @JsonProperty(access = Access.READ_ONLY)
  protected LocalDateTime lastTryTime;
  @JsonProperty(access = Access.READ_ONLY)
  protected Long challengeId;
  @JsonProperty(access = Access.READ_ONLY)
  protected Boolean isSolved;

  public static CtfCommonChallengeDto toDto(CtfChallengeEntity challenge, Boolean isSolved,
      CtfFlagEntity ctfFlagEntity) {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        challenge.getCtfChallengeCategoryEntity());

    return CtfCommonChallengeDto.builder()
        .challengeId(challenge.getId())
        .title(challenge.getName())
        .contestId(challenge.getCtfContestEntity().getId())
        .category(category)
        .score(challenge.getScore())
        .isSolved(isSolved)
        .remainedSubmitCount(ctfFlagEntity.getRemainedSubmitCount())
        .lastTryTime(ctfFlagEntity.getLastTryTime())
        .maxSubmitCount(challenge.getMaxSubmitCount())
        .build();
  }
}
