package keeper.project.homepage.ctf.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ctf_dynamic_challenge_info")
public class CtfDynamicChallengeInfoEntity {

  @Id
  @Column(name = "challenge_id")
  private Long challengeId;

  @OneToOne
  @PrimaryKeyJoinColumn(name = "challenge_id", referencedColumnName = "id")
  private CtfChallengeEntity ctfChallengeEntity;

  @Column
  @Setter
  private Long maxScore;

  @Column
  @Setter
  private Long minScore;
}
