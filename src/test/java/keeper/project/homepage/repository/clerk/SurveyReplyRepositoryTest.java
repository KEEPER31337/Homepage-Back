package keeper.project.homepage.repository.clerk;

import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.ACTIVITY;
import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.GRADUATE;
import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.LEAVE;
import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.MILITARY_DORMANT;
import static keeper.project.homepage.clerk.entity.SurveyReplyEntity.SurveyReply.OTHER_DORMANT;

import java.util.List;
import keeper.project.homepage.clerk.entity.SurveyReplyEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SurveyReplyRepositoryTest extends SurveyRepositoryTestHelper {

  @Test
  @DisplayName("응답 종류 테스트")
  public void SurveyReplyTest() {
    //given
    // 1: "활동" 2: "휴면(군휴학)" 3: "휴면(기타)" 4: "졸업" 5: "탈퇴"

    //when
    SurveyReplyEntity activity = surveyReplyRepository.getById(ACTIVITY.getId());
    SurveyReplyEntity militaryDormant = surveyReplyRepository.getById(MILITARY_DORMANT.getId());
    SurveyReplyEntity otherDormant = surveyReplyRepository.getById(OTHER_DORMANT.getId());
    SurveyReplyEntity graduate = surveyReplyRepository.getById(GRADUATE.getId());
    SurveyReplyEntity leave = surveyReplyRepository.getById(LEAVE.getId());

    //then
    Assertions.assertThat(activity.getType()).isEqualTo(ACTIVITY.getType());
    Assertions.assertThat(militaryDormant.getType()).isEqualTo(MILITARY_DORMANT.getType());
    Assertions.assertThat(otherDormant.getType()).isEqualTo(OTHER_DORMANT.getType());
    Assertions.assertThat(graduate.getType()).isEqualTo(GRADUATE.getType());
    Assertions.assertThat(leave.getType()).isEqualTo(LEAVE.getType());

  }

  @Test
  @DisplayName("응답 종류 개수 테스트")
  public void NumberOfReplyTest() {
    //given
    // 1: "활동" 2: "휴면(군휴학)" 3: "휴면(기타)" 4: "졸업" 5: "탈퇴"

    //when
    List<SurveyReplyEntity> replyEntityList = surveyReplyRepository.findAll();

    //then
    Assertions.assertThat(replyEntityList.size()).isEqualTo(5);

  }
}
