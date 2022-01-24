package keeper.project.homepage.controller.sign;

import keeper.project.homepage.dto.CommonResult;
import keeper.project.homepage.dto.SingleResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.sign.SignInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
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
      @RequestParam String loginId,
      @RequestParam String password) {

    MemberEntity memberEntity = signInService.login(loginId, password);
    String token = signInService.createJwtToken(memberEntity);
    return responseService.getSingleResult(token);
  }

  @PostMapping(value = "/change-password")
  public CommonResult changePassword(
      @RequestParam String newPassword
  ) {
    signInService.changePassword(newPassword);
    return responseService.getSuccessResult();
  }
}
