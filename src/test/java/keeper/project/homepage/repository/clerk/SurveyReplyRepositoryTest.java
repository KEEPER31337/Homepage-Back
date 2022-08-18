package keeper.project.homepage.repository.clerk.survey;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.entity.clerk.survey.SurveyReplyEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SurveyReplyRepositoryTest extends SurveyTestHelper{
  @Test
  @DisplayName("응답 종류 조회 테스트")
  void viewReplyTest(){
    //given
    // 1: "활동" 2: "휴면(군휴학)" 3: "휴면(기타)" 4: "졸업" 5: "탈퇴"

    //when
    List<SurveyReplyEntity> surveyReplyEntities = new ArrayList<>();
    surveyReplyEntities.add(surveyReplyRepository.findById(1L).get());
    surveyReplyEntities.add(surveyReplyRepository.findById(2L).get());
    surveyReplyEntities.add(surveyReplyRepository.findById(3L).get());
    surveyReplyEntities.add(surveyReplyRepository.findById(4L).get());
    surveyReplyEntities.add(surveyReplyRepository.findById(5L).get());

    //then
    Assertions.assertThat(surveyReplyEntities.size()).isEqualTo(5);
    Assertions.assertThat(surveyReplyEntities.get(0).getType()).isEqualTo("활동");
    Assertions.assertThat(surveyReplyEntities.get(1).getType()).isEqualTo("휴면(군휴학)");
    Assertions.assertThat(surveyReplyEntities.get(2).getType()).isEqualTo("휴면(기타)");
    Assertions.assertThat(surveyReplyEntities.get(3).getType()).isEqualTo("졸업");
    Assertions.assertThat(surveyReplyEntities.get(4).getType()).isEqualTo("탈퇴");

  }
}
