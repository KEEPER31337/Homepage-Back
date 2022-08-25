package keeper.project.homepage.entity.clerk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

@Entity
@Getter
@Setter
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
  private SurveyMemberReplyEntity surveyMemberReplyEntity;

  @Column(nullable = false, length = 200)
  private String restExcuse;

}
