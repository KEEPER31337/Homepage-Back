package keeper.project.homepage.repository.clerk;

import keeper.project.homepage.entity.clerk.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {

  SurveyEntity findTopByOrderByIdDesc();
}
