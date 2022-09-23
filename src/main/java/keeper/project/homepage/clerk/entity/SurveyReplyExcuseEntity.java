package keeper.project.homepage.clerk.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "survey_reply_excuse")
public class SurveyReplyExcuseEntity {

  @Id
  @Column(name = "survey_member_reply_id")
  private Long id;

  @OneToOne
  @MapsId
  @JoinColumn(name = "survey_member_reply_id")
  @Setter
  private SurveyMemberReplyEntity surveyMemberReplyEntity;

  @Column(nullable = false, length = 200)
  @Setter
  @Nullable
  private String restExcuse;

  public static SurveyReplyExcuseEntity newInstance(
      SurveyMemberReplyEntity surveyMemberReply, String restExcuse) {
    return SurveyReplyExcuseEntity.builder()
        .surveyMemberReplyEntity(surveyMemberReply)
        .restExcuse(restExcuse)
        .build();
  }

}
