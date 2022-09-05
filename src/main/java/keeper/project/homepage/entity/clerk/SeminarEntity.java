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
import keeper.project.homepage.admin.dto.clerk.request.AttendanceStartRequestDto;
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

  /**
   * null이면 DB에서 TRIGGER로 이름이 자동 생성 형식은 생성 날짜의 yyyyMMdd 패턴 ex) 20220901
   */
  @Column(length = 100)
  private String name;

  @Column(nullable = false)
  private LocalDateTime openTime;

  private LocalDateTime attendanceCloseTime;

  private LocalDateTime latenessCloseTime;

  @Column(length = 10)
  private String attendanceCode;

  @Builder.Default
  @OneToMany(mappedBy = "seminarEntity")
  List<SeminarAttendanceEntity> seminarAttendances = new ArrayList<>();

  public void startAttendance(LocalDateTime attendanceCloseTime, LocalDateTime latenessCloseTime,
      String attendanceCode) {
    this.attendanceCloseTime = attendanceCloseTime;
    this.latenessCloseTime = latenessCloseTime;
    this.attendanceCode = attendanceCode;
  }

}
