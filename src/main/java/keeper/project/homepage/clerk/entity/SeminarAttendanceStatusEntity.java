package keeper.project.homepage.clerk.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "seminar_attendance_status")
public class SeminarAttendanceStatusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String type;

  @Getter
  @RequiredArgsConstructor
  public enum SeminarAttendanceStatus {
    ATTENDANCE(1L, "출석"),
    LATENESS(2L, "지각"),
    ABSENCE(3L, "결석"),
    PERSONAL(4L, "개인사정"),
    BEFORE_ATTENDANCE(5L, "출석 전");
    private final Long id;
    private final String type;
  }
}
