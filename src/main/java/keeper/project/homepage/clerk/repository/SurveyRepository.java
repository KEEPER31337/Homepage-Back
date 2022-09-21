package keeper.project.homepage.clerk.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import keeper.project.homepage.clerk.entity.SurveyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {

  /**
   * 현재 진행중 + 공개 된 설문 중 마감 시간이 가장 늦은 설문 하나만 가져옵니다
   *
   * @param currTime1 현재 시각을 넣으면 됩니다.
   * @param currTime2 현재 시각을 넣으면 됩니다. Spring Data JPA 특성상 같은 값이어도 두 번 들어가기 때문에 인자를 2개로 받습니다.
   * @return 위 조건에 해당하는 결과를 반환합니다.
   */
  Optional<SurveyEntity> findTop1ByOpenTimeBeforeAndCloseTimeAfterAndIsVisibleTrueOrderByCloseTimeDesc(
      LocalDateTime currTime1,
      LocalDateTime currTime2);

  /**
   * 종료 + 공개 된 설문 중 마감 시간이 가장 늦은 설문 하나만 가져옵니다
   *
   * @param currTime 현재 시각을 넣으면 됩니다.
   * @return 위 조건에 해당하는 결과를 반환합니다.
   */
  Optional<SurveyEntity> findTop1ByCloseTimeBeforeAndIsVisibleTrueOrderByCloseTimeDesc(
      LocalDateTime currTime);

  Page<SurveyEntity> findAllByIdIsNot(Long surveyId, Pageable pageable);

}
