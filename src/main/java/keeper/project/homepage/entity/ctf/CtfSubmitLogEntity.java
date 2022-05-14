package keeper.project.homepage.entity.ctf;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.member.MemberEntity;
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
@Table(name = "ctf_submit_log")
public class CtfSubmitLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  Long id;

  @Column(nullable = false)
  LocalDateTime submitTime;

  @OneToOne
  @JoinColumn(name = "team_id")
  CtfTeamEntity ctfTeamEntity;

  @OneToOne
  @JoinColumn
  MemberEntity submitter;

  @OneToOne
  @JoinColumn(name = "challenge_id")
  CtfChallengeEntity ctfChallengeEntity;

  @Column(nullable = false)
  @Setter
  String flagSubmitted;

  @Column(nullable = false)
  @Setter
  Boolean isCorrect;
}
