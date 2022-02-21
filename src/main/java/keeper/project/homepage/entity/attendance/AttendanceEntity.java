package keeper.project.homepage.entity.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
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
import lombok.Setter;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attendance")
public class AttendanceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "time", nullable = false)
  private Date time;

  @Setter
  @Column(name = "point", nullable = false)
  private Integer point;

  @Setter
  @Column(name = "rank_point", nullable = false)
  private Integer rankPoint;

  @Column(name = "continuous_point", nullable = false)
  private Integer continuousPoint;

  @Column(name = "random_point", nullable = false)
  private Integer randomPoint;

  @Column(name = "ip_address", nullable = false, length = 128)
  private String ipAddress;

  @Setter
  @Column(name = "greetings", length = 250)
  private String greetings;

  @Column(name = "continuous_day", nullable = false)
  private Integer continuousDay;

  // 엔티티에 자바의 예약어와 동일한 컬럼이 있다면 같은 오류가 발생할 수 있다.
  // 위와 같은 경우는 ``로 감싸준다.
  @Setter
  @Column(name = "`rank`")
  private Integer rank;

  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "member_id", nullable = false)
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private MemberEntity member;

}