package keeper.project.homepage.entity.clerk;

import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "survey_member_reply")
public class SurveyMemberReplyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "survey_id", nullable = false)
  private SurveyEntity survey;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reply_id", nullable = false)
  @Setter
  private SurveyReplyEntity reply;

  @OneToOne(mappedBy = "surveyMemberReplyEntity", cascade = CascadeType.ALL)
  @PrimaryKeyJoinColumn
  @Setter
  private SurveyReplyExcuseEntity surveyReplyExcuseEntity;

  @Column(nullable = false)
  @Setter
  private LocalDateTime replyTime;

  public void setSurveyMemberReply(SurveyReplyExcuseEntity surveyReplyExcuseEntity) {
    this.setSurveyReplyExcuseEntity(surveyReplyExcuseEntity);
    surveyReplyExcuseEntity.setSurveyMemberReplyEntity(this);
  }

}
