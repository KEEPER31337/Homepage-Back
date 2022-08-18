package keeper.project.homepage.entity.seminar;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seminar_attendance_excuse")
public class SeminarAttendanceExcuseEntity{

  @Id
  @Column(name = "seminar_attendance_id")
  private Long id;

  @OneToOne
  @MapsId
  @JoinColumn(name = "seminar_attendance_id", referencedColumnName = "id")
  private SeminarAttendanceEntity seminarAttendanceEntity;

  @NotNull
  @Column(length = 200)
  private String absenceExcuse;

}

