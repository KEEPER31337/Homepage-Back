package keeper.project.homepage.user.controller.study;

import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/study")
public class StudyController {

  private final StudyService studyService;
  private final ResponseService responseService;

  @GetMapping("/years")
  public ListResult<Integer> getYears() {

    return responseService.getSuccessListResult(
        studyService.getAllStudyYears());
  }
}
