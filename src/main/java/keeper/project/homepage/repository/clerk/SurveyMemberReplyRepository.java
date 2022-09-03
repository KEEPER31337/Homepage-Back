package keeper.project.homepage.repository.clerk;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyMemberReplyRepository extends JpaRepository<SurveyMemberReplyEntity, Long> {

  Optional<List<SurveyMemberReplyEntity>> findAllBySurveyId(Long surveyId);

  Optional<SurveyMemberReplyEntity> findBySurveyIdAndMemberId(Long surveyId, Long memberId);

}
