package keeper.project.homepage.repository.clerk;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyMemberReplyRepository extends JpaRepository<SurveyMemberReplyEntity, Long> {

  List<SurveyMemberReplyEntity> findAllBySurveyId(Long surveyId);

  SurveyMemberReplyEntity findByMemberId(Long memberId);

  SurveyMemberReplyEntity findBySurveyId(Long surveyId);
}
