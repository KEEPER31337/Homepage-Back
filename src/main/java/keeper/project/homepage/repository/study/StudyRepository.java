package keeper.project.homepage.repository.study;

import java.util.List;
import keeper.project.homepage.entity.study.StudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyRepository extends JpaRepository<StudyEntity, Long> {

  @Query("SELECT DISTINCT year FROM StudyEntity")
  List<Integer> findDistinctYear();

  @Query("SELECT DISTINCT season FROM StudyEntity where year=?1")
  List<Integer> findDistinctSeasonByYear(Integer year);

  List<StudyEntity> findAllByYearAndSeason(Integer year, Integer season);
}
