package keeper.project.homepage.controller.sign;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.dto.CommonResult;
import keeper.project.homepage.dto.SingleResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.repository.member.MemberRankRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import keeper.project.homepage.service.sign.DuplicateCheckService;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.sign.SignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
      @RequestParam String loginId,
      @RequestParam String emailAddress,
      @RequestParam String password,
      @RequestParam String realName,
      @RequestParam String nickName,
      @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") @Nullable Date birthday,
      @RequestParam String studentId
  ) {

    signUpService.signUp(loginId, emailAddress, password, realName, nickName, birthday, studentId);
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
