package keeper.project.homepage.common.controller.sign;

import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.common.dto.sign.EmailAuthDto;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.common.service.sign.SignInService;
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
      @RequestBody MemberDto memberDto) {

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
      @RequestBody MemberDto memberDto
  ) {
    signInService.changePassword(memberDto.getPassword());
    return responseService.getSuccessResult();
  }
}
