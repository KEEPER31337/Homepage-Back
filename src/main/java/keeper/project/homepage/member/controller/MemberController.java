package keeper.project.homepage.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import keeper.project.homepage.member.dto.response.MemberFollowResponseDto;
import keeper.project.homepage.member.dto.response.UserMemberResponseDto;
import keeper.project.homepage.member.dto.response.MultiMemberResponseDto;
import keeper.project.homepage.member.dto.response.OtherMemberInfoResponseDto;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.service.MemberDeleteService;
import keeper.project.homepage.member.service.MemberFindService;
import keeper.project.homepage.member.service.MemberFollowService;
import keeper.project.homepage.member.service.MemberService;
import keeper.project.homepage.posting.dto.PostingResponseDto;
import keeper.project.homepage.posting.service.PostingService;
import keeper.project.homepage.sign.dto.EmailAuthDto;
import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.util.dto.result.ListResult;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.util.service.result.ResponseService;
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
  private final MemberFindService memberFindService;
  private final MemberDeleteService memberDeleteService;
  private final MemberFollowService memberFollowService;
  private final ResponseService responseService;
  private final AuthService authService;
  private final PostingService postingService;

  @Secured("ROLE_회원")
  @GetMapping(value = "/others")
  public ListResult<OtherMemberInfoResponseDto> getOtherMembers() {
    MemberEntity loginMember = authService.getMemberEntityWithJWT();
    return responseService.getSuccessListResult(memberFindService.getOtherMembers(loginMember));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/others/{id}")
  public SingleResult<OtherMemberInfoResponseDto> getOtherMember(
      @PathVariable("id") Long otherMemberId
  ) {
    MemberEntity loginMember = authService.getMemberEntityWithJWT();
    return responseService.getSuccessSingleResult(
        memberFindService.getOtherMember(loginMember, otherMemberId));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/multi")
  public ListResult<MultiMemberResponseDto> getMultiMembers(
      @RequestParam List<Long> ids
  ) {
    return responseService.getSuccessListResult(memberFindService.getMultiMembers(ids));
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/profile")
  public SingleResult<UserMemberResponseDto> getMember() {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    // 결과데이터가 단일건인경우 getSuccessSingleResult 이용해서 결과를 출력한다.
    return responseService.getSuccessSingleResult(memberFindService.getMember(id));
  }

  @Secured("ROLE_회원")
  @PutMapping(value = "/profile")
  public SingleResult<UserMemberResponseDto> updateProfile(
      @RequestBody UserMemberResponseDto memberDto) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    UserMemberResponseDto updated = memberService.updateProfile(memberDto, id);
    return responseService.getSuccessSingleResult(updated);
  }

  @Secured("ROLE_회원")
  @PutMapping(value = "/thumbnail")
  public SingleResult<UserMemberResponseDto> updateThumbnail(
      @RequestParam("thumbnail") MultipartFile image,
      @RequestParam("ipAddress") String ipAddress) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    UserMemberResponseDto updated = memberService.updateThumbnails(id, image, ipAddress);
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
  public SingleResult<UserMemberResponseDto> updateEmailAddress(
      @RequestBody UserMemberResponseDto memberDto) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    // 실제 존재하는 email인지 인증 코드를 통해 확인
    UserMemberResponseDto updated = memberService.updateEmailAddress(memberDto, id);
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
    memberFollowService.follow(id, memberId);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @DeleteMapping(value = "/unfollow/{id}")
  public CommonResult unfollowByLoginId(@PathVariable("id") Long memberId) {
    Long id = authService.getMemberIdByJWT();
    memberFollowService.unfollow(id, memberId);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/followers")
  public ListResult<UserMemberResponseDto> showFollowerList() {
    Long id = authService.getMemberIdByJWT();
    List<UserMemberResponseDto> followerList = memberFollowService.showFollower(id);
    return responseService.getSuccessListResult(followerList);
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/followees")
  public ListResult<UserMemberResponseDto> showFolloweeList() {
    Long id = authService.getMemberIdByJWT();
    List<UserMemberResponseDto> followeeList = memberFollowService.showFollowee(id);
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
  public SingleResult<MemberFollowResponseDto> getFollowerAndFolloweeCount() {
    Long id = authService.getMemberIdByJWT();
    MemberFollowResponseDto followDto = memberFollowService.getFollowerAndFolloweeNumber(id);
    return responseService.getSuccessSingleResult(followDto);
  }

  @Secured("ROLE_회원")
  @GetMapping("/generations")
  public ListResult<Float> getAllGenerations() {
    return responseService.getSuccessListResult(memberService.getAllGenerations());
  }
}