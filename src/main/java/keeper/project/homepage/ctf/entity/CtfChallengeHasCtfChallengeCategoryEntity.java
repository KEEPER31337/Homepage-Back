package keeper.project.homepage.ctf.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CtfChallengeHasCtfChallengeCategoryEntityPK.class) // 정의한 idclass 주입
@Table(name = "ctf_challenge_has_ctf_challenge_category")
public class CtfChallengeHasCtfChallengeCategoryEntity implements Serializable {

  @Id
  @ManyToOne(targetEntity = CtfChallengeEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "ctf_challenge_id")
  private CtfChallengeEntity challenge;

  @Id
  @ManyToOne(targetEntity = CtfChallengeCategoryEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "ctf_challenge_category_id")
  private CtfChallengeCategoryEntity category;

}
