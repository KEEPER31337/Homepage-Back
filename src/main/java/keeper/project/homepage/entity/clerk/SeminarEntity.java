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
import lombok.Setter;

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

  // null 이면 db 에서 trigger 로 이름이 자동 생성 됩니다.
  private String name;
  @Setter
  @Column(length = 10)
  private String attendanceCode;

  @NotNull
  private LocalDateTime openTime;
  @Setter
  private LocalDateTime attendanceCloseTime;
  @Setter
  private LocalDateTime latenessCloseTime;

  @Builder.Default
  @OneToMany(mappedBy = "seminarEntity")
  List<SeminarAttendanceEntity> seminarAttendances = new ArrayList<>();

}
