package keeper.project.homepage.about.controller;

import keeper.project.homepage.admin.service.about.AdminStaticWriteTitleService;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.about.dto.request.StaticWriteTitleDto;
import keeper.project.homepage.about.dto.response.StaticWriteTitleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin/about/titles")
public class AdminStaticWriteTitleController {

  private final AdminStaticWriteTitleService adminStaticWriteTitleService;
  private final ResponseService responseService;

  @Secured("ROLE_회장")
  @RequestMapping(
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      value = "/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public SingleResult<StaticWriteTitleResponseDto> updateTitleById(
      @PathVariable("id") Long id,
      @RequestBody StaticWriteTitleDto titleDto) {

    return responseService.getSuccessSingleResult(
        adminStaticWriteTitleService.updateTitleById(titleDto, id));
  }

}
