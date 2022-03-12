package keeper.project.homepage.common.controller.member;

import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.common.service.member.CommonMemberService;
import keeper.project.homepage.user.dto.member.OtherMemberInfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
