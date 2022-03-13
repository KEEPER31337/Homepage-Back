package keeper.project.homepage.user.service.study;

import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.admin.dto.category.result.CategoryResult;
import keeper.project.homepage.entity.study.StudyEntity;
import keeper.project.homepage.repository.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyService {

  private final StudyRepository studyRepository;

  public List<Integer> getAllStudyYears() {
    return studyRepository.findDistinctYear();
  }
}
