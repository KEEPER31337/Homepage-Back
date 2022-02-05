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

  @Column(name = "point", nullable = false)
  private Integer point;

  @Column(name = "random_point", nullable = false)
  private Integer randomPoint;

  @Column(name = "ip_address", nullable = false, length = 128)
  private String ipAddress;

  @Setter
  @Column(name = "greetings", length = 250)
  private String greetings;

  @Column(name = "continous_day", nullable = false)
  private Integer continousDay;

  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "member_id", nullable = false)
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private MemberEntity memberId;

}