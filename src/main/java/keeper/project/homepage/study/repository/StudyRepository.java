package keeper.project.homepage.study.repository;

import java.util.List;
import keeper.project.homepage.study.entity.StudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyRepository extends JpaRepository<StudyEntity, Long> {

  @Query("SELECT DISTINCT year FROM StudyEntity")
  List<Integer> findDistinctYear();

  @Query("SELECT DISTINCT season FROM StudyEntity where year=?1")
  List<Integer> findDistinctSeasonByYear(Integer year);

  List<StudyEntity> findAllByYearAndSeason(Integer year, Integer season);
}
