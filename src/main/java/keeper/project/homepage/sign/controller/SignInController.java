package keeper.project.homepage.sign.controller;

import keeper.project.homepage.member.dto.response.UserMemberResponseDto;
import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.sign.dto.EmailAuthDto;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.sign.dto.SignInDto;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.sign.service.SignInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Secured("ROLE_USER") // 모든 url에 공통 설정
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/signin")
public class SignInController {

  private final ResponseService responseService;
  private final SignInService signInService;

  @PostMapping(value = "")
  public SingleResult<SignInDto> signIn(
      @RequestBody UserMemberResponseDto memberDto) {

    MemberEntity memberEntity = signInService.login(memberDto.getLoginId(),
        memberDto.getPassword());
    String token = signInService.createJwtToken(memberEntity);
    SignInDto result = signInService.createSignInDto(token, memberEntity);
    return responseService.getSuccessSingleResult(result);
  }

  @PostMapping(value = "/find-id")
  public CommonResult findIdWithEmail(
      @RequestBody EmailAuthDto emailAuthDto
  ) {
    signInService.findIdWithEmail(emailAuthDto);
    return responseService.getSuccessResult();
  }

  @PostMapping(value = "/find-password")
  public CommonResult findPasswordWithEmail(
      @RequestBody EmailAuthDto emailAuthDto
  ) {
    signInService.findPasswordWithEmail(emailAuthDto);
    return responseService.getSuccessResult();
  }

  @PostMapping(value = "/change-password")
  public CommonResult changePassword(
      @RequestBody UserMemberResponseDto memberDto
  ) {
    signInService.changePassword(memberDto.getPassword());
    return responseService.getSuccessResult();
  }
}
