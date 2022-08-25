package keeper.project.homepage.repository.clerk;

import static keeper.project.homepage.controller.clerk.SurveySpringTestHelper.Reply.ACTIVITY;
import static keeper.project.homepage.controller.clerk.SurveySpringTestHelper.Reply.GRADUATE;
import static keeper.project.homepage.controller.clerk.SurveySpringTestHelper.Reply.LEAVE;
import static keeper.project.homepage.controller.clerk.SurveySpringTestHelper.Reply.MILITARY_DORMANT;
import static keeper.project.homepage.controller.clerk.SurveySpringTestHelper.Reply.OTHER_DORMANT;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SurveyReplyRepositoryTest extends SurveyRepositoryTestHelper {

  @Test
  @DisplayName("응답 종류 조회 테스트")
  void viewReplyTest() {
    //given
    // 1: "활동" 2: "휴면(군휴학)" 3: "휴면(기타)" 4: "졸업" 5: "탈퇴"

    //when
    List<SurveyReplyEntity> surveyReplyEntities = new ArrayList<>();
    surveyReplyEntities.add(surveyReplyRepository.findById(ACTIVITY.getReplyId()).get());
    surveyReplyEntities.add(surveyReplyRepository.findById(MILITARY_DORMANT.getReplyId()).get());
    surveyReplyEntities.add(surveyReplyRepository.findById(OTHER_DORMANT.getReplyId()).get());
    surveyReplyEntities.add(surveyReplyRepository.findById(GRADUATE.getReplyId()).get());
    surveyReplyEntities.add(surveyReplyRepository.findById(LEAVE.getReplyId()).get());

    //then
    Assertions.assertThat(surveyReplyEntities.size()).isEqualTo(5);
    Assertions.assertThat(surveyReplyEntities.get(0).getType()).isEqualTo("활동");
    Assertions.assertThat(surveyReplyEntities.get(1).getType()).isEqualTo("휴면(군휴학)");
    Assertions.assertThat(surveyReplyEntities.get(2).getType()).isEqualTo("휴면(기타)");
    Assertions.assertThat(surveyReplyEntities.get(3).getType()).isEqualTo("졸업");
    Assertions.assertThat(surveyReplyEntities.get(4).getType()).isEqualTo("탈퇴");

  }
}
