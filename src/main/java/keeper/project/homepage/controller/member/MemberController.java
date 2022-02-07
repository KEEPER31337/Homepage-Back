package keeper.project.homepage.controller.member;

import javax.servlet.http.HttpServletResponse;
import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//@Secured("ROLE_USER") // 모든 url에 공통 설정
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class MemberController {

  private final MemberRepository memberRepository;
  private final ResponseService responseService;
  private final AuthService authService;
  private final MemberService memberService;

  @Secured("ROLE_회장") // 각 리소스별 권한 설정
  @GetMapping(value = "/members")
  public ListResult<MemberEntity> findAllMember() {
    // 결과데이터가 여러건인경우 getSuccessListResult 이용해서 결과를 출력한다.
    return responseService.getSuccessListResult(memberRepository.findAll());
  }

  @Secured("ROLE_회원") // 각 리소스별 권한 설정
  @GetMapping(value = "/member")
  public SingleResult<MemberEntity> findMember() {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long id = Long.valueOf(authentication.getName());
    // 결과데이터가 단일건인경우 getSuccessSingleResult 이용해서 결과를 출력한다.
    return responseService.getSuccessSingleResult(
        memberRepository.findById(id).orElseThrow(CustomMemberNotFoundException::new));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/member/post")
  public ListResult<PostingDto> findAllPosting(
      @SortDefault(sort = "registerDate", direction = Direction.ASC)
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    Long id = authService.getMemberIdByJWT();

    Page<PostingDto> page = memberService.findAllPostingByIsTemp(id, pageable,
        PostingService.isNotTempPosting);
    return responseService.getSuccessListResult(page.getContent());
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/member/temp_post")
  public ListResult<PostingDto> findAllTempPosting(
      @SortDefault(sort = "registerDate", direction = Direction.ASC)
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    Long id = authService.getMemberIdByJWT();

    Page<PostingDto> page = memberService.findAllPostingByIsTemp(id, pageable,
        PostingService.isTempPosting);
    return responseService.getSuccessListResult(page.getContent());
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/member/temp_post/{pid}")
  public void findPosting(@PathVariable("pid") Long postingId, HttpServletResponse response) {
    String uri = "/v1/post/" + postingId;
    try {
      response.sendRedirect(uri);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Secured("ROLE_회원")
  @RequestMapping(method = {RequestMethod.PUT,
      RequestMethod.PATCH}, value = "/member/post/{pid}")
  public void modifyPosting(@PathVariable("pid") Long postingId, HttpServletResponse response) {
    String uri = "/v1/post/" + postingId.toString();
    try {
      response.sendRedirect(uri);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Secured("ROLE_회원")
  @DeleteMapping(value = "/member/post/{pid}")
  public void deletePosting(@PathVariable("pid") Long postingId, HttpServletResponse response) {
    String uri = "/v1/post/" + postingId.toString();
    try {
      response.sendRedirect(uri);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
