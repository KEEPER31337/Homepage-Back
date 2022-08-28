package keeper.project.homepage.entity.clerk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "survey_reply")
public class SurveyReplyEntity {

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 10)
  private String type;

  @Getter
  @RequiredArgsConstructor
  public enum SurveyReply {
    ACTIVITY(1L, "활동"),
    MILITARY_DORMANT(2L, "휴면(군휴학)"),
    OTHER_DORMANT(3L, "휴면(기타)"),
    GRADUATE(4L, "졸업"),
    LEAVE(5L, "탈퇴");

    private final Long id;
    private final String type;
  }
}
