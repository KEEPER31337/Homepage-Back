package keeper.project.homepage.user.controller.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import keeper.project.homepage.common.dto.sign.EmailAuthDto;
import keeper.project.homepage.user.dto.member.MemberDto;
import keeper.project.homepage.user.dto.member.MemberFollowDto;
import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.user.dto.member.MultiMemberResponseDto;
import keeper.project.homepage.user.dto.member.OtherMemberInfoResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.user.dto.posting.PostingResponseDto;
import keeper.project.homepage.user.service.member.MemberDeleteService;
import keeper.project.homepage.user.service.posting.PostingService;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.user.service.member.MemberService;
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

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/members")
public class MemberController {

  private final MemberService memberService;
  private final MemberDeleteService memberDeleteService;
  private final ResponseService responseService;
  private final AuthService authService;
  private final PostingService postingService;

  @Secured("ROLE_회원")
  @GetMapping(value = "/others")
  public ListResult<OtherMemberInfoResult> getOtherMembers() {
    return responseService.getSuccessListResult(memberService.getOtherMembers());
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/others/{id}")
  public SingleResult<OtherMemberInfoResult> getOtherMember(
      @PathVariable("id") Long otherMemberId
  ) {
    return responseService.getSuccessSingleResult(
        memberService.getOtherMember(otherMemberId));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/multi")
  public ListResult<MultiMemberResponseDto> getMultiMembers(
      @RequestParam List<Long> ids
  ) {
    return responseService.getSuccessListResult(memberService.getMultiMembers(ids));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/profile")
  public SingleResult<MemberDto> getMember() {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    // 결과데이터가 단일건인경우 getSuccessSingleResult 이용해서 결과를 출력한다.
    return responseService.getSuccessSingleResult(memberService.getMember(id));
  }

  @Secured("ROLE_회원")
  @PutMapping(value = "/profile")
  public SingleResult<MemberDto> updateProfile(@RequestBody MemberDto memberDto) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    MemberDto updated = memberService.updateProfile(memberDto, id);
    return responseService.getSuccessSingleResult(updated);
  }

  @Secured("ROLE_회원")
  @PutMapping(value = "/thumbnail")
  public SingleResult<MemberDto> updateThumbnail(
      @RequestParam("thumbnail") MultipartFile image,
      @RequestParam("ipAddress") String ipAddress) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    MemberDto updated = memberService.updateThumbnails(id, image, ipAddress);
    return responseService.getSuccessSingleResult(updated);
  }

  @PostMapping(value = "/emailauth")
  public CommonResult emailAuth(@RequestBody EmailAuthDto emailAuthDto) {
    EmailAuthDto emailAuthDtoForSend = memberService.generateEmailAuth(emailAuthDto);
    memberService.sendEmailAuthCode(emailAuthDtoForSend);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @PutMapping(value = "/email")
  public SingleResult<MemberDto> updateEmailAddress(@RequestBody MemberDto memberDto) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    // 실제 존재하는 email인지 인증 코드를 통해 확인
    MemberDto updated = memberService.updateEmailAddress(memberDto, id);
    return responseService.getSuccessSingleResult(updated);
  }

  // TODO : registerTime 기준으로 정렬이 제대로 안됨.
  @Secured("ROLE_회원")
  @GetMapping(value = "/posts")
  public SingleResult<Map<String, Object>> findAllPosting(
      @SortDefault(sort = "registerTime", direction = Direction.DESC)
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    Long id = authService.getMemberIdByJWT();

    Map<String, Object> result = new HashMap<>();
    Page<PostingResponseDto> page = memberService.findAllPostingByIsTemp(id, pageable,
        PostingService.isNotTempPosting);
    result.put("isLast", page.isLast());
    result.put("content", page.getContent());
    return responseService.getSuccessSingleResult(result);
  }

  // TODO : registerTime 기준으로 정렬이 제대로 안됨.
  @Secured("ROLE_회원")
  @GetMapping(value = "/temp_posts")
  public SingleResult<Map<String, Object>> findAllTempPosting(
      @SortDefault(sort = "registerTime", direction = Direction.DESC)
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    Long id = authService.getMemberIdByJWT();

    Map<String, Object> result = new HashMap<>();
    Page<PostingResponseDto> page = memberService.findAllPostingByIsTemp(id, pageable,
        PostingService.isTempPosting);
    result.put("isLast", page.isLast());
    result.put("content", page.getContent());
    return responseService.getSuccessSingleResult(result);
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/posts/{pid}")
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
      RequestMethod.PATCH}, value = "/posts/{pid}")
  public void modifyPosting(@PathVariable("pid") Long postingId, HttpServletResponse response) {
    String uri = "/v1/post/" + postingId.toString();
    try {
      response.sendRedirect(uri);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Secured("ROLE_회원")
  @DeleteMapping(value = "/posts/{pid}")
  public void deletePosting(@PathVariable("pid") Long postingId, HttpServletResponse response) {
    String uri = "/v1/post/" + postingId.toString();
    try {
      response.sendRedirect(uri);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Secured("ROLE_회원")
  @PostMapping(value = "/follow/{id}")
  public CommonResult followByLoginId(@PathVariable("id") Long memberId) {
    Long id = authService.getMemberIdByJWT();
    memberService.follow(id, memberId);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @DeleteMapping(value = "/unfollow/{id}")
  public CommonResult unfollowByLoginId(@PathVariable("id") Long memberId) {
    Long id = authService.getMemberIdByJWT();
    memberService.unfollow(id, memberId);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/followers")
  public ListResult<MemberDto> showFollowerList() {
    Long id = authService.getMemberIdByJWT();
    List<MemberDto> followerList = memberService.showFollower(id);
    return responseService.getSuccessListResult(followerList);
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/followees")
  public ListResult<MemberDto> showFolloweeList() {
    Long id = authService.getMemberIdByJWT();
    List<MemberDto> followeeList = memberService.showFollowee(id);
    return responseService.getSuccessListResult(followeeList);
  }

  @Secured("ROLE_회원")
  @DeleteMapping("")
  public CommonResult deleteMember(@RequestParam("password") String password) {
    Long id = authService.getMemberIdByJWT();
    memberDeleteService.deleteAccount(id, password);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @GetMapping("/{memberId}/posts")
  public SingleResult<Map<String, Object>> findPostingListOfOther(
      @PathVariable("memberId") Long memberId,
      @PageableDefault(size = 10, page = 0, sort = "registerTime", direction = Direction.DESC) Pageable pageable
  ) {
    return responseService.getSuccessSingleResult(
        postingService.findAllByMemberId(memberId, pageable));
  }

  @Secured("ROLE_회원")
  @GetMapping("/{memberId}/posts/{postId}")
  public SingleResult<PostingResponseDto> findSinglePostingOfOther(
      @PathVariable("memberId") Long memberId,
      @PathVariable("postId") Long postId) {
    PostingResponseDto posting = postingService.getPostingResponseById(postId,
        authService.getMemberIdByJWT(), null);
    System.out.println("In Controller : " + posting.getId());
    return responseService.getSuccessSingleResult(posting);
  }

  @Secured("ROLE_회원")
  @GetMapping("/follow-number")
  public SingleResult<MemberFollowDto> getFollowerAndFolloweeCount() {
    Long id = authService.getMemberIdByJWT();
    MemberFollowDto followDto = memberService.getFollowerAndFolloweeNumber(id);
    return responseService.getSuccessSingleResult(followDto);
  }
}