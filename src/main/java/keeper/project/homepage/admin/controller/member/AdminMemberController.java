package keeper.project.homepage.admin.controller.member;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import keeper.project.homepage.dto.EmailAuthDto;
import keeper.project.homepage.dto.member.MemberDemeritDto;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.dto.member.MemberGenerationDto;
import keeper.project.homepage.dto.member.MemberJobDto;
import keeper.project.homepage.dto.member.MemberMeritDto;
import keeper.project.homepage.dto.member.MemberRankDto;
import keeper.project.homepage.dto.member.MemberTypeDto;
import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.dto.request.PointTransferRequest;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.OtherMemberInfoResult;
import keeper.project.homepage.dto.result.PointTransferResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.member.MemberService;
import keeper.project.homepage.service.posting.PostingService;
import keeper.project.homepage.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//@Secured("ROLE_USER") // 모든 url에 공통 설정
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/admin")
public class AdminMemberController {

  private final MemberService memberService;
  private final ResponseService responseService;
  private final AuthService authService;

  @Secured("ROLE_회장") // 각 리소스별 권한 설정
  @GetMapping(value = "/members")
  public ListResult<MemberEntity> findAllMember() {
    // 결과데이터가 여러건인경우 getSuccessListResult 이용해서 결과를 출력한다.
    return responseService.getSuccessListResult(memberService.findAll());
  }

  @Secured("ROLE_회장")
  @PutMapping("/member/update/rank")
  public SingleResult<MemberDto> updateMemberRank(@RequestBody MemberRankDto memberRankDto) {
    // FIXME : Dto 인자로 넣으면서 loginId는 왜 뺐지;;; loginId 삭제하기
    String loginId = memberRankDto.getMemberLoginId();
    MemberDto update = memberService.updateMemberRank(memberRankDto, loginId);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured("ROLE_회장")
  @PutMapping("/member/update/type")
  public SingleResult<MemberDto> updateMemberType(@RequestBody MemberTypeDto memberTypeDto) {
    // FIXME : Dto 인자로 넣으면서 loginId는 왜 뺐지;;; loginId 삭제하기
    String loginId = memberTypeDto.getMemberLoginId();
    MemberDto update = memberService.updateMemberType(memberTypeDto, loginId);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured("ROLE_회장")
  @PutMapping("/member/update/job")
  public SingleResult<MemberDto> updateMemberJob(@RequestBody MemberJobDto memberJobDto) {

    // FIXME : Dto 인자로 넣으면서 loginId는 왜 뺐지;;; loginId 삭제하기
    String loginId = memberJobDto.getMemberLoginId();
    MemberDto update = memberService.updateMemberJobs(memberJobDto, loginId);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/member/update/generation")
  public SingleResult<MemberDto> updateMemberGeneration(
      @RequestBody MemberGenerationDto memberGenerationDto) {
    MemberDto update = memberService.updateGeneration(memberGenerationDto);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/member/update/merit")
  public SingleResult<MemberDto> updateMemberMerit(@RequestBody MemberMeritDto memberMeritDto) {
    MemberDto update = memberService.updateMerit(memberMeritDto);
    return responseService.getSuccessSingleResult(update);
  }


  @Secured({"ROLE_회장", "ROLE_서기"})
  @PutMapping("/member/update/demerit")
  public SingleResult<MemberDto> updateMemberDemerit(
      @RequestBody MemberDemeritDto memberDemeritDto) {
    MemberDto update = memberService.updateDemerit(memberDemeritDto);
    return responseService.getSuccessSingleResult(update);
  }
}
