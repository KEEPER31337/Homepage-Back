package keeper.project.homepage.entity.clerk;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seminar_attendance_status")
public class SeminarAttendanceStatusEntity {
  public static final SeminarAttendanceStatusEntity ATTENDANCE = new SeminarAttendanceStatusEntity(1L, "ATTENDANCE");
  public static final SeminarAttendanceStatusEntity LATENESS = new SeminarAttendanceStatusEntity(2L, "LATENESS");
  public static final SeminarAttendanceStatusEntity ABSENCE = new SeminarAttendanceStatusEntity(3L, "ABSENCE");
  public static final SeminarAttendanceStatusEntity PERSONAL = new SeminarAttendanceStatusEntity(4L, "PERSONAL");

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String type;
}
