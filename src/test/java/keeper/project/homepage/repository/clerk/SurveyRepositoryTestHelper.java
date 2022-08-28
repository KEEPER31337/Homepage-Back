package keeper.project.homepage.repository.clerk;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyExcuseEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class SurveyRepositoryTestHelper {

  @Autowired
  protected EntityManager em;

  @Autowired
  protected MemberRepository memberRepository;

  @Autowired
  protected SurveyMemberReplyRepository surveyMemberReplyRepository;
  @Autowired
  protected SurveyReplyExcuseRepository surveyReplyExcuseRepository;
  @Autowired
  protected SurveyReplyRepository surveyReplyRepository;
  @Autowired
  protected SurveyRepository surveyRepository;

  protected SurveyEntity generateSurvey(LocalDateTime openTime, LocalDateTime closeTime,
      Boolean isVisible) {
    final long epochTime = System.nanoTime();
    return surveyRepository.save(
        SurveyEntity.builder()
            .name("name_" + epochTime)
            .openTime(openTime)
            .closeTime(closeTime)
            .description("description_" + epochTime)
            .isVisible(isVisible)
            .build()
    );
  }

  protected SurveyMemberReplyEntity generateSurveyMemberReply(SurveyEntity survey,
      MemberEntity member,
      SurveyReplyEntity reply) {
    return surveyMemberReplyRepository.save(
        SurveyMemberReplyEntity.builder()
            .member(member)
            .survey(survey)
            .reply(reply)
            .replyTime(LocalDateTime.now())
            .build()
    );
  }

  protected SurveyReplyExcuseEntity generateSurveyReplyExcuse(
      SurveyMemberReplyEntity surveyMemberReplyEntity, String because) {
    SurveyReplyExcuseEntity excuse = SurveyReplyExcuseEntity.builder()
        .surveyMemberReplyEntity(surveyMemberReplyEntity)
        .restExcuse(because)
        .build();

    surveyMemberReplyEntity.assignSurveyReplyExcuseEntity(excuse);

    return surveyReplyExcuseRepository.save(excuse);

  }
}
