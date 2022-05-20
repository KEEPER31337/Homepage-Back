package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import java.util.ArrayList;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.member.MemberEntity;
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
@JsonInclude(Include.NON_NULL)
public class CtfCommonChallengeDto {

  @JsonProperty(access = Access.READ_ONLY)
  protected Long challengeId;
  protected String title;
  protected Long score;
  protected Long creatorId;
  protected CtfChallengeCategoryDto category;
  protected Long contestId;

  public static CtfCommonChallengeDto toDto(CtfChallengeEntity challenge) {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        challenge.getCtfChallengeCategoryEntity());

    return CtfCommonChallengeDto.builder()
        .challengeId(challenge.getId())
        .title(challenge.getName())
        .contestId(challenge.getCtfContestEntity().getId())
        .category(category)
        .creatorId(challenge.getCreator().getId())
        .score(challenge.getScore())
        .build();
  }
}
