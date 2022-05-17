package keeper.project.homepage.entity.ctf;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name = "ctf_flag")
public class CtfFlagEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  Long id;

  @Column(nullable = false, length = 200)
  String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  CtfTeamEntity ctfTeamEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "challenge_id")
  CtfChallengeEntity ctfChallengeEntity;

  @Column(nullable = false)
  @Setter
  Boolean isCorrect;
}
