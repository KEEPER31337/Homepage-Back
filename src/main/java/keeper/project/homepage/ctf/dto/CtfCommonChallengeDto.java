package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
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
public class CtfCommonChallengeDto {

  protected String title;
  protected Long score;
  protected CtfChallengeCategoryDto category;
  protected Long contestId;

  @JsonProperty(access = Access.READ_ONLY)
  protected Long challengeId;
  @JsonProperty(access = Access.READ_ONLY)
  protected Boolean isSolved;

  public static CtfCommonChallengeDto toDto(CtfChallengeEntity challenge, Boolean isSolved) {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        challenge.getCtfChallengeCategoryEntity());

    return CtfCommonChallengeDto.builder()
        .challengeId(challenge.getId())
        .title(challenge.getName())
        .contestId(challenge.getCtfContestEntity().getId())
        .category(category)
        .score(challenge.getScore())
        .isSolved(isSolved)
        .build();
  }
}
