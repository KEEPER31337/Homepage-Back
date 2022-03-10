package keeper.project.homepage.admin.controller.member;

import keeper.project.homepage.admin.service.member.AdminMemberService;
import keeper.project.homepage.admin.dto.member.MemberDemeritDto;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.admin.dto.member.MemberGenerationDto;
import keeper.project.homepage.admin.dto.member.MemberJobDto;
import keeper.project.homepage.admin.dto.member.MemberMeritDto;
import keeper.project.homepage.admin.dto.member.MemberRankDto;
import keeper.project.homepage.admin.dto.member.MemberTypeDto;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.service.ResponseService;
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
@RequestMapping(value = "/v1/admin")
public class AdminMemberController {

  private final AdminMemberService adminMemberService;
  private final ResponseService responseService;

  @Secured("ROLE_회장") // 각 리소스별 권한 설정
  @GetMapping(value = "/members")
  public ListResult<MemberEntity> findAllMember() {
    // 결과데이터가 여러건인경우 getSuccessListResult 이용해서 결과를 출력한다.
    return responseService.getSuccessListResult(adminMemberService.findAll());
  }

  @Secured("ROLE_회장")
  @PutMapping("/member/update/rank")
  public SingleResult<MemberDto> updateMemberRank(@RequestBody MemberRankDto memberRankDto) {
    MemberDto update = adminMemberService.updateMemberRank(memberRankDto);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured("ROLE_회장")
  @PutMapping("/member/update/type")
  public SingleResult<MemberDto> updateMemberType(@RequestBody MemberTypeDto memberTypeDto) {
    MemberDto update = adminMemberService.updateMemberType(memberTypeDto);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured("ROLE_회장")
  @PutMapping("/member/update/job")
  public SingleResult<MemberDto> updateMemberJob(@RequestBody MemberJobDto memberJobDto) {

    MemberDto update = adminMemberService.updateMemberJobs(memberJobDto);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/member/update/generation")
  public SingleResult<MemberDto> updateMemberGeneration(
      @RequestBody MemberGenerationDto memberGenerationDto) {
    MemberDto update = adminMemberService.updateGeneration(memberGenerationDto);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/member/update/merit")
  public SingleResult<MemberDto> updateMemberMerit(@RequestBody MemberMeritDto memberMeritDto) {
    MemberDto update = adminMemberService.updateMerit(memberMeritDto);
    return responseService.getSuccessSingleResult(update);
  }


  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/member/update/demerit")
  public SingleResult<MemberDto> updateMemberDemerit(
      @RequestBody MemberDemeritDto memberDemeritDto) {
    MemberDto update = adminMemberService.updateDemerit(memberDemeritDto);
    return responseService.getSuccessSingleResult(update);
  }
}