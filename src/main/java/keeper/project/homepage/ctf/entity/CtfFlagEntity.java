package keeper.project.homepage.ctf.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

  @Column(name = "solved_time")
  @Setter
  LocalDateTime solvedTime;

  @Column(name = "last_submit_time")
  LocalDateTime lastSubmitTime;

  @Column(name = "remaining_submit_count")
  Long remainingSubmitCount;

  public void updateLastSubmitTime() {
    lastSubmitTime = LocalDateTime.now();
  }

  public void decreaseSubmitCount() {
    if (remainingSubmitCount <= 0) {
      throw new IllegalStateException("제출 횟수가 0이하이기 때문에 제출 횟수를 감소시킬 수 없습니다.");
    }
    --remainingSubmitCount;
  }
}
