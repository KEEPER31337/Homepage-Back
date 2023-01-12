package keeper.project.homepage.sign.controller;

import keeper.project.homepage.member.dto.response.UserMemberResponseDto;
import keeper.project.homepage.util.dto.result.CommonResult;
import keeper.project.homepage.sign.dto.EmailAuthDto;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.sign.service.DuplicateCheckService;
import keeper.project.homepage.util.service.result.ResponseService;
import keeper.project.homepage.sign.service.SignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@Secured("ROLE_USER") // 모든 url에 공통 설정
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/signup")
public class SignUpController {

  private final DuplicateCheckService duplicateCheckService;
  private final ResponseService responseService;
  private final SignUpService signUpService;

  @PostMapping(value = "")
  public CommonResult signUp(
      @RequestBody UserMemberResponseDto memberDto
  ) {
    signUpService.signUpWithEmailAuthCode(memberDto);
    return responseService.getSuccessResult();
  }

  @PostMapping(value = "/emailauth")
  public CommonResult emailAuth(
      @RequestBody EmailAuthDto emailAuthDto
  ) {

    EmailAuthDto emailAuthDtoForSend = signUpService.generateEmailAuth(emailAuthDto);
    signUpService.sendEmailAuthCode(emailAuthDtoForSend);
    return responseService.getSuccessResult();
  }

  @GetMapping(value = "/checkloginidduplication")
  public SingleResult<Boolean> checkLoginIdDuplication(
      @RequestParam String loginId
  ) {

    return responseService.getSuccessSingleResult(
        duplicateCheckService.isLoginIdDuplicate(loginId)
    );
  }

  @GetMapping(value = "/checkemailaddressduplication")
  public SingleResult<Boolean> checkEmailAddressDuplication(
      @RequestParam String emailAddress
  ) {

    return responseService.getSuccessSingleResult(
        duplicateCheckService.isEmailAddressDuplicate(emailAddress)
    );
  }

  @GetMapping(value = "/checkstudentidduplication")
  public SingleResult<Boolean> checkStudentIdDuplication(
      @RequestParam String studentId
  ) {

    return responseService.getSuccessSingleResult(
        duplicateCheckService.isStudentIdDuplicate(studentId)
    );
  }
}
