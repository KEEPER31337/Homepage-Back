package keeper.project.homepage.entity.clerk;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "seminar_attendance")
public class SeminarAttendanceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private MemberEntity memberEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seminar_id")
  private SeminarEntity seminarEntity;

  @ManyToOne
  @JoinColumn(name = "status_id")
  private SeminarAttendanceStatusEntity seminarAttendanceStatusEntity;

  @PrimaryKeyJoinColumn
  @OneToOne(mappedBy = "seminarAttendanceEntity")
  private SeminarAttendanceExcuseEntity seminarAttendanceExcuseEntity;

  @NotNull
  @Column(name = "attend_time")
  private LocalDateTime seminarAttendTime;
}
