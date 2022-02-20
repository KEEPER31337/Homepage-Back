package keeper.project.homepage.controller.member;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import keeper.project.homepage.dto.EmailAuthDto;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.dto.member.MemberJobDto;
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
import keeper.project.homepage.entity.posting.PostingEntity;
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
@RequestMapping(value = "/v1")
public class MemberController {

  private final MemberService memberService;
  private final ResponseService responseService;
  private final AuthService authService;

  @Secured("ROLE_회장") // 각 리소스별 권한 설정
  @GetMapping(value = "/admin/members")
  public ListResult<MemberEntity> findAllMember() {
    // 결과데이터가 여러건인경우 getSuccessListResult 이용해서 결과를 출력한다.
    return responseService.getSuccessListResult(memberService.findAll());
  }

  @Secured("ROLE_회원") // 각 리소스별 권한 설정
  @GetMapping(value = "/member")
  public SingleResult<MemberEntity> findMember() {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    // 결과데이터가 단일건인경우 getSuccessSingleResult 이용해서 결과를 출력한다.
    return responseService.getSuccessSingleResult(memberService.findById(id));
  }

  @Secured("ROLE_회원") // 각 리소스별 권한 설정
  @GetMapping(value = "/members")
  public ListResult<OtherMemberInfoResult> getAllGeneralMemberInfo() {
    return responseService.getSuccessListResult(memberService.getAllGeneralMemberInfo());
  }

  @Secured("ROLE_회원")
  @PutMapping(value = "/member/update/profile")
  public SingleResult<MemberDto> updateProfile(@RequestBody MemberDto memberDto) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    MemberDto updated = memberService.updateProfile(memberDto, id);
    return responseService.getSuccessSingleResult(updated);
  }

  @Secured("ROLE_회원")
  @PutMapping(value = "/member/update/thumbnail")
  public SingleResult<MemberDto> updateThumbnail(
      @RequestParam("thumbnail") MultipartFile image,
      @RequestParam("ipAddress") String ipAddress) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    MemberDto updated = memberService.updateThumbnails(id, image, ipAddress);
    return responseService.getSuccessSingleResult(updated);
  }

  @PostMapping(value = "/member/update/emailauth")
  public CommonResult emailAuth(@RequestBody EmailAuthDto emailAuthDto) {
    EmailAuthDto emailAuthDtoForSend = memberService.generateEmailAuth(emailAuthDto);
    memberService.sendEmailAuthCode(emailAuthDtoForSend);
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @PutMapping(value = "/member/update/email")
  public SingleResult<MemberDto> updateEmailAddress(@RequestBody MemberDto memberDto) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Long id = authService.getMemberIdByJWT();
    // 실제 존재하는 email인지 인증 코드를 통해 확인
    MemberDto updated = memberService.updateEmailAddress(memberDto, id);
    return responseService.getSuccessSingleResult(updated);
  }

  @Secured("ROLE_회장")
  @PutMapping("member/update/rank")
  public SingleResult<MemberDto> updateMemberRank(@RequestBody MemberRankDto memberRankDto) {
    String loginId = memberRankDto.getMemberLoginId();
    MemberDto update = memberService.updateMemberRank(memberRankDto, loginId);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured("ROLE_회장")
  @PutMapping("member/update/type")
  public SingleResult<MemberDto> updateMemberType(@RequestBody MemberTypeDto memberTypeDto) {
    String loginId = memberTypeDto.getMemberLoginId();
    MemberDto update = memberService.updateMemberType(memberTypeDto, loginId);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured("ROLE_회장")
  @PutMapping("member/update/job")
  public SingleResult<MemberDto> updateMemberJob(@RequestBody MemberJobDto memberJobDto) {
    String loginId = memberJobDto.getMemberLoginId();
    MemberDto update = memberService.updateMemberJobs(memberJobDto, loginId);
    return responseService.getSuccessSingleResult(update);
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/member/post")
  public ListResult<PostingEntity> findAllPosting(
      @SortDefault(sort = "registerDate", direction = Direction.ASC)
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    Long id = authService.getMemberIdByJWT();

    Page<PostingEntity> page = memberService.findAllPostingByIsTemp(id, pageable,
        PostingService.isNotTempPosting);
    return responseService.getSuccessListResult(page.getContent());
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/member/temp_post")
  public ListResult<PostingEntity> findAllTempPosting(
      @SortDefault(sort = "registerDate", direction = Direction.ASC)
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    Long id = authService.getMemberIdByJWT();

    Page<PostingEntity> page = memberService.findAllPostingByIsTemp(id, pageable,
        PostingService.isTempPosting);
    return responseService.getSuccessListResult(page.getContent());
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/member/post/{pid}")
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

  @Secured("ROLE_회원")
  @PostMapping(value = "/member/follow")
  public CommonResult followByLoginId(@RequestBody MemberDto memberDto) {
    Long id = authService.getMemberIdByJWT();
    memberService.follow(id, memberDto.getFolloweeLoginId());
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @DeleteMapping(value = "/member/unfollow")
  public CommonResult unfollowByLoginId(@RequestBody MemberDto memberDto) {
    Long id = authService.getMemberIdByJWT();
    memberService.unfollow(id, memberDto.getFolloweeLoginId());
    return responseService.getSuccessResult();
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/member/follower")
  public ListResult<MemberDto> showFollowerList() {
    Long id = authService.getMemberIdByJWT();
    List<MemberDto> followerList = memberService.showFollower(id);
    return responseService.getSuccessListResult(followerList);
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/member/followee")
  public ListResult<MemberDto> showFolloweeList() {
    Long id = authService.getMemberIdByJWT();
    List<MemberDto> followeeList = memberService.showFollowee(id);
    return responseService.getSuccessListResult(followeeList);
  }

  @Secured("ROLE_회원")
  @GetMapping(value = "/member/other/info/{id}")
  public SingleResult<OtherMemberInfoResult> getOtherMemberInfo(
      @PathVariable("id") Long id
  ) {
    return responseService.getSuccessSingleResult(memberService.getOtherMemberInfo(id));

  }

  @Secured("ROLE_회원")
  @PutMapping("/member/update/point/transfer")
  public SingleResult<PointTransferResult> transferPoint(
      @RequestBody PointTransferRequest pointTransferRequest
  ) {
    Long senderId = authService.getMemberIdByJWT();
    return responseService.getSuccessSingleResult(
        memberService.transferPoint(senderId, pointTransferRequest));
  }
}
