package keeper.project.homepage.entity.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game_member_info")
public class GameEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToOne(targetEntity = MemberEntity.class, fetch = FetchType.EAGER)
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
  }
}