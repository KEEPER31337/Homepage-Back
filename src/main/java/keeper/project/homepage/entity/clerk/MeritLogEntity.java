package keeper.project.homepage.entity.clerk;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "merit_log")
public class MeritLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "awarder_id")
  private MemberEntity awarder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "giver_id")
  private MemberEntity giver;

  @Column(name = "time")
  private LocalDate date;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merit_type_id")
  private MeritTypeEntity meritType;

  public void changeMeritType(MeritTypeEntity meritType) {
    this.meritType = meritType;
  }

  public void changeDate(LocalDate date) {
    this.date = date;
  }
}
