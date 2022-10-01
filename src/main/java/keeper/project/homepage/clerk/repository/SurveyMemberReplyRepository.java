package keeper.project.homepage.clerk.repository;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.clerk.entity.SurveyMemberReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyMemberReplyRepository extends JpaRepository<SurveyMemberReplyEntity, Long> {

  Optional<List<SurveyMemberReplyEntity>> findAllBySurveyId(Long surveyId);

  Optional<SurveyMemberReplyEntity> findBySurveyIdAndMemberId(Long surveyId, Long memberId);

}
