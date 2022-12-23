package keeper.project.homepage.ctf.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CtfChallengeHasCtfChallengeCategoryEntityPK implements Serializable {

  private Long challenge;
  private Long category;
}
