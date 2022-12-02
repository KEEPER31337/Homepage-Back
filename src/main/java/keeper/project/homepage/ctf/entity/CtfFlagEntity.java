package keeper.project.homepage.ctf.entity;

import java.time.LocalDateTime;
import java.util.Optional;
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
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@DynamicInsert
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

  @Column(name = "last_try_time")
  LocalDateTime lastTryTime;

  @Column(name = "remained_submit_count")
  Long remainedSubmitCount;

  public void updateLastTryTime() {
    lastTryTime = LocalDateTime.now();
  }

  public void decreaseSubmitCount() {
    if (remainedSubmitCount <= 0) {
      throw new IllegalStateException("제출 횟수를 모두 소진하셨기 때문에 제출 횟수를 감소시킬 수 없습니다.");
    }
    --remainedSubmitCount;
  }

  public Optional<LocalDateTime> getLastTryTime() {
    return Optional.ofNullable(lastTryTime);
  }
}
