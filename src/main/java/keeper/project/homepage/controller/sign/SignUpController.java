package keeper.project.homepage.controller.sign;

import java.util.Collections;
import java.util.Date;
import keeper.project.homepage.dto.CommonResult;
import keeper.project.homepage.dto.SingleResult;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.repository.MemberRepository;
import keeper.project.homepage.service.DuplicateCheckService;
import keeper.project.homepage.service.ResponseService;
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

  private final MemberRepository memberRepository;
  private final DuplicateCheckService duplicateCheckService;
  private final ResponseService responseService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping(value = "")
  public CommonResult signUp(
      @RequestParam String loginId,
      @RequestParam String emailAddress,
      @RequestParam String password,
      @RequestParam String realName,
      @RequestParam @Nullable String nickName,
      @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") @Nullable Date birthday,
      @RequestParam String studentId
  ) {

    memberRepository.save(MemberEntity.builder()
        .loginId(loginId)
        .emailAddress(emailAddress)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .birthday(birthday)
        .studentId(studentId)
        .roles(Collections.singletonList("ROLE_USER"))
        .build());
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
