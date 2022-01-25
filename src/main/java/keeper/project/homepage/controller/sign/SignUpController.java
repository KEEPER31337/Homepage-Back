package keeper.project.homepage.controller.sign;

import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.dto.EmailAuthDto;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.service.sign.DuplicateCheckService;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.sign.SignUpService;
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
      @RequestBody MemberDto memberDto
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

    return responseService.getSingleResult(
        duplicateCheckService.checkLoginIdDuplicate(loginId)
    );
  }

  @GetMapping(value = "/checkemailaddressduplication")
  public SingleResult<Boolean> checkEmailAddressDuplication(
      @RequestParam String emailAddress
  ) {

    return responseService.getSingleResult(
        duplicateCheckService.checkEmailAddressDuplicate(emailAddress)
    );
  }

  @GetMapping(value = "/checkstudentidduplication")
  public SingleResult<Boolean> checkStudentIdDuplication(
      @RequestParam String studentId
  ) {

    return responseService.getSingleResult(
        duplicateCheckService.checkStudentIdDuplicate(studentId)
    );
  }
}
