package keeper.project.homepage.entity.clerk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "merit_type")
public class MeritTypeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer merit;

  private Boolean is_merit;

  @Column(length = 45)
  private String detail;

  @Getter
  @RequiredArgsConstructor
  public enum MeritType {

    PUBLIC_ANNOUNCEMENT(1L, "각종대외발표"),
    BEST_TECH_DOC(2L, "우수기술문서작성"),
    MANY_TECH_DOC(3L, "연2개이상의기술문서작성"),
    BEST_STUDY(4L, "우수스터디진행"),
    WIN_A_CONTEST(5L, "전공관련대회입상"),
    ATTENDANCE_AWARD(6L, "개근상"),
    ABSENCE(7L, "무단결석");

    private final Long id;
    private final String detail;
  }
}
