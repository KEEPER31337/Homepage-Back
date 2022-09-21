package keeper.project.homepage.member.controller;

import keeper.project.homepage.member.dto.CommonMemberDto;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.member.service.CommonMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/common")
public class CommonMemberController {

  private final CommonMemberService commonMemberService;
  private final ResponseService responseService;

  @GetMapping(value = "/members")
  public ListResult<CommonMemberDto> getAllCommonMemberInfo() {
    return responseService.getSuccessListResult(commonMemberService.getAllCommonMemberInfo());
  }
}
