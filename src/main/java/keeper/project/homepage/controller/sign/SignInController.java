package keeper.project.homepage.controller.sign;

import keeper.project.homepage.dto.CommonResult;
import keeper.project.homepage.dto.EmailAuthDto;
import keeper.project.homepage.dto.MemberDto;
import keeper.project.homepage.dto.SingleResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.sign.SignInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public SingleResult<String> signIn(
      @RequestBody MemberDto memberDto) {

    MemberEntity memberEntity = signInService.login(memberDto.getLoginId(),
        memberDto.getPassword());
    String token = signInService.createJwtToken(memberEntity);
    return responseService.getSingleResult(token);
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
