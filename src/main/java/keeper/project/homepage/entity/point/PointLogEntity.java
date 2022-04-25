package keeper.project.homepage.entity.point;

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
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "point_log")
public class PointLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @Column(name = "`time`", nullable = false)
  private LocalDateTime time;

  @Column(name = "point", nullable = false)
  private Integer point;

  @Column(name = "detail", length = 45)
  private String detail;

  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "presented")
  private MemberEntity presentedMember;

  @Column(name = "is_spent", nullable = false)
  private Integer isSpent;

}
