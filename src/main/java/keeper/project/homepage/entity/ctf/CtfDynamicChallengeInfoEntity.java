package keeper.project.homepage.entity.ctf;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ctf_dynamic_challenge_info")
public class CtfDynamicChallengeInfoEntity {

  @Id
  @OneToOne
  @JoinColumn(name = "challenge_id")
  CtfChallengeEntity ctfChallengeEntity;

  @Column
  private Long maxScore;

  @Column
  private Long minScore;
}
