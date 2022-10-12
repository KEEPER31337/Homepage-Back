package keeper.project.homepage.attendance.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import keeper.project.homepage.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game_member_info")
public class GameEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToOne(targetEntity = MemberEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private MemberEntity member;

  @Column(name = "dice_per_day", nullable = false)
  private Integer dicePerDay;

  @Column(name = "roulette_per_day", nullable = false)
  private Integer roulettePerDay;

  @Column(name = "lotto_per_day", nullable = false)
  private Integer lottoPerDay;

  @Column(name = "last_play_time")
  private LocalDateTime lastPlayTime;

  @Setter
  @Column(name = "dice_day_point")
  private Integer diceDayPoint;

  @Setter
  @Column(name = "roulette_day_point")
  private Integer rouletteDayPoint;

  @Setter
  @Column(name = "lotto_day_point")
  private Integer lottoDayPoint;

  public void increaseDiceTimes() {
    dicePerDay += 1;
  }

  public void increaseRouletteTimes() {
    roulettePerDay += 1;
  }

  public void increaseLottoTimes() {
    lottoPerDay += 1;
  }

  public void setLastPlayTime(LocalDateTime time) {
    lastPlayTime = time;
  }

  public void reset() {
    dicePerDay = 0;
    roulettePerDay = 0;
    lottoPerDay = 0;
    diceDayPoint = 0;
    rouletteDayPoint = 0;
    lottoDayPoint = 0;
  }
}