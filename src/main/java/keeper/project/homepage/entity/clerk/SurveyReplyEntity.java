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

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "survey_reply")
public class SurveyReplyEntity {

  public static final SurveyReplyEntity ACTIVITY = new SurveyReplyEntity(1L, "활동");
  public static final SurveyReplyEntity MILITARY_DORMANT = new SurveyReplyEntity(2L,
      "휴면(군휴학)");
  public static final SurveyReplyEntity OTHER_DORMANT = new SurveyReplyEntity(3L, "휴면(기타)");
  public static final SurveyReplyEntity GRADUATE = new SurveyReplyEntity(4L, "졸업");
  public static final SurveyReplyEntity LEAVE = new SurveyReplyEntity(5L, "탈퇴");

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 10)
  private String type;
}
