package keeper.project.homepage.clerk.repository;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import keeper.project.homepage.clerk.entity.SurveyEntity;
import keeper.project.homepage.clerk.entity.SurveyMemberReplyEntity;
import keeper.project.homepage.clerk.entity.SurveyReplyEntity;
import keeper.project.homepage.clerk.entity.SurveyReplyExcuseEntity;
import keeper.project.homepage.clerk.repository.SurveyMemberReplyRepository;
import keeper.project.homepage.clerk.repository.SurveyReplyExcuseRepository;
import keeper.project.homepage.clerk.repository.SurveyReplyRepository;
import keeper.project.homepage.clerk.repository.SurveyRepository;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.repository.MemberRepository;
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

    surveyMemberReplyEntity.setSurveyReplyExcuseEntity(excuse);

    return surveyReplyExcuseRepository.save(excuse);

  }
}
