package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfDynamicChallengeInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class CtfDynamicChallengeInfoDto {

  private Long maxScore;
  private Long minScore;

  public static CtfDynamicChallengeInfoDto toDto(CtfDynamicChallengeInfoEntity dynamicInfo) {
    if (dynamicInfo == null) {
      return new CtfDynamicChallengeInfoDto();
    }
    return CtfDynamicChallengeInfoDto.builder()
        .maxScore(dynamicInfo.getMaxScore())
        .minScore(dynamicInfo.getMinScore())
        .build();
  }

  public CtfDynamicChallengeInfoEntity toEntity(CtfChallengeEntity challenge) {
    return CtfDynamicChallengeInfoEntity.builder()
        .challengeId(challenge.getId())
        .ctfChallengeEntity(challenge)
        .maxScore(maxScore)
        .minScore(minScore)
        .build();
  }
}
