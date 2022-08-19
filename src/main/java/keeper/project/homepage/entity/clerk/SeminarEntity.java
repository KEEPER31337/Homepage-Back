package keeper.project.homepage.entity.clerk;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "seminar")
public class SeminarEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String name;

  @Column(length = 10)
  private String attendanceCode;

  @NotNull
  private LocalDateTime openTime;

  private LocalDateTime attendanceCloseTime;

  private LocalDateTime latenessCloseTime;

  @Builder.Default
  @OneToMany(mappedBy = "seminarEntity")
  List<SeminarAttendanceEntity> seminarAttendanceEntity = new ArrayList<>();

}
