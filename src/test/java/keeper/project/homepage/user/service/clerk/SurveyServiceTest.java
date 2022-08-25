package keeper.project.homepage.user.service.clerk;

import static keeper.project.homepage.controller.clerk.SurveySpringTestHelper.Reply.ACTIVITY;
import static keeper.project.homepage.controller.clerk.SurveySpringTestHelper.Reply.GRADUATE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import keeper.project.homepage.ApiControllerTestHelper.MemberJobName;
import keeper.project.homepage.ApiControllerTestHelper.MemberRankName;
import keeper.project.homepage.ApiControllerTestHelper.MemberTypeName;
import keeper.project.homepage.controller.clerk.SurveySpringTestHelper;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SurveyServiceTest extends SurveySpringTestHelper {
  @Autowired
  private EntityManager em;

  private MemberEntity user;
  private MemberEntity admin;

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
  }

  @Test
  @DisplayName("설문 응답")
  public void responseSurvey() throws Exception {
    //given
    SurveyEntity survey = surveyRepository.getById(1L);
    SurveyMemberReplyEntity surveyMemberReplyEntity = generateSurveyMemberReply(survey,user,surveyReplyRepository.getById(GRADUATE.getReplyId()));

    //when
    SurveyEntity findSurvey = surveyRepository.getById(survey.getId());

    //then
    assertThat(findSurvey.getRespondents()).contains(surveyMemberReplyEntity);
  }
}
