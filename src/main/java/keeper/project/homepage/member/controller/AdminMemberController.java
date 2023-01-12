package keeper.project.homepage.member.controller;

import keeper.project.homepage.member.service.AdminMemberService;
import keeper.project.homepage.member.dto.request.MemberDemeritRequestDto;
import keeper.project.homepage.member.dto.MemberDto;
import keeper.project.homepage.member.dto.request.MemberGenerationRequestDto;
import keeper.project.homepage.member.dto.request.MemberJobRequestDto;
import keeper.project.homepage.member.dto.request.MemberMeritRequestDto;
import keeper.project.homepage.member.dto.request.MemberRankRequestDto;
import keeper.project.homepage.member.dto.request.MemberTypeRequestDto;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.result.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public SingleResult<MemberDto> updateMemberRank(@RequestBody MemberRankRequestDto memberRankRequestDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateMemberRank(memberRankRequestDto));
  }

  @Secured("ROLE_회장")
  @PutMapping("/type")
  public SingleResult<MemberDto> updateMemberType(@RequestBody MemberTypeRequestDto memberTypeRequestDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateMemberType(memberTypeRequestDto));
  }

  @Secured("ROLE_회장")
  @PutMapping("/job")
  public SingleResult<MemberDto> updateMemberJob(@RequestBody MemberJobRequestDto memberJobRequestDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateMemberJobs(memberJobRequestDto));
  }

  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/generation")
  public SingleResult<MemberDto> updateMemberGeneration(
      @RequestBody MemberGenerationRequestDto memberGenerationRequestDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateGeneration(memberGenerationRequestDto));
  }

  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/merit")
  public SingleResult<MemberDto> updateMemberMerit(@RequestBody MemberMeritRequestDto memberMeritRequestDto) {
    return responseService.getSuccessSingleResult(adminMemberService.updateMerit(
        memberMeritRequestDto));
  }

  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/demerit")
  public SingleResult<MemberDto> updateMemberDemerit(
      @RequestBody MemberDemeritRequestDto memberDemeritRequestDto) {
    return responseService.getSuccessSingleResult(
        adminMemberService.updateDemerit(memberDemeritRequestDto));
  }

}