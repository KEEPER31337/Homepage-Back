package keeper.project.homepage.ctf.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "ctf_submit_log")
public class CtfSubmitLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  LocalDateTime submitTime;

  @Column(nullable = false, length = 200)
  String flagSubmitted;

  @Column(nullable = false)
  Boolean isCorrect;

  @Column(nullable = false, length = 45)
  String teamName;

  @Column(nullable = false, length = 80)
  String submitterLoginId;

  @Column(nullable = false, length = 45)
  String submitterRealname;

  @Column(nullable = false, length = 200)
  String challengeName;

  @Column(nullable = false, length = 45)
  String contestName;

  @OneToOne
  @JoinColumn(name = "ctf_contest_id")
  CtfContestEntity contest;
}
