package keeper.project.homepage.admin.controller.member;

import keeper.project.homepage.admin.service.member.AdminMemberService;
import keeper.project.homepage.admin.dto.member.MemberDemeritDto;
import keeper.project.homepage.admin.dto.member.MemberDto;
import keeper.project.homepage.admin.dto.member.MemberGenerationDto;
import keeper.project.homepage.admin.dto.member.MemberJobDto;
import keeper.project.homepage.admin.dto.member.MemberMeritDto;
import keeper.project.homepage.admin.dto.member.MemberRankDto;
import keeper.project.homepage.admin.dto.member.MemberTypeDto;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/admin/members")
public class AdminMemberController {

  private final AdminMemberService adminMemberService;
  private final ResponseService responseService;

  @Secured("ROLE_회장") // 각 리소스별 권한 설정
  @GetMapping(value = "")
  public ListResult<MemberDto> getMembers() {
    // 결과데이터가 여러건인경우 getSuccessListResult 이용해서 결과를 출력한다.
    return responseService.getSuccessListResult(adminMemberService.getMembers());
  }

  @Secured("ROLE_회장")
  @PutMapping("/rank")
  public SingleResult<MemberDto> updateMemberRank(@RequestBody MemberRankDto memberRankDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateMemberRank(memberRankDto));
  }

  @Secured("ROLE_회장")
  @PutMapping("/type")
  public SingleResult<MemberDto> updateMemberType(@RequestBody MemberTypeDto memberTypeDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateMemberType(memberTypeDto));
  }

  @Secured("ROLE_회장")
  @PutMapping("/job")
  public SingleResult<MemberDto> updateMemberJob(@RequestBody MemberJobDto memberJobDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateMemberJobs(memberJobDto));
  }

  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/generation")
  public SingleResult<MemberDto> updateMemberGeneration(
      @RequestBody MemberGenerationDto memberGenerationDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateGeneration(memberGenerationDto));
  }

  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/merit")
  public SingleResult<MemberDto> updateMemberMerit(@RequestBody MemberMeritDto memberMeritDto) {
    return responseService.getSuccessSingleResult(adminMemberService.updateMerit(memberMeritDto));
  }


  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/demerit")
  public SingleResult<MemberDto> updateMemberDemerit(
      @RequestBody MemberDemeritDto memberDemeritDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateDemerit(memberDemeritDto));
  }

  @Secured({"ROLE_회장", "ROLE_부회장", "ROLE_서기"})
  @GetMapping("/ids")
  public ListResult<Long> getMemberIdsByRealName(@RequestParam String keyword) {
    return responseService.getSuccessListResult(adminMemberService.getMemberIdsByRealName(keyword));
  }
}