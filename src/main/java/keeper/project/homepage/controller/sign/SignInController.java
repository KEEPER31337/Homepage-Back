package keeper.project.homepage.controller.sign;

import keeper.project.homepage.config.security.JwtTokenProvider;
import keeper.project.homepage.dto.SingleResult;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.exception.CustomLoginIdSigninFailedException;
import keeper.project.homepage.repository.MemberRepository;
import keeper.project.homepage.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final ResponseService responseService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping(value = "")
  public SingleResult<String> signIn(
      @RequestParam String loginId,
      @RequestParam String password) {
    MemberEntity memberEntity = memberRepository.findByLoginId(loginId)
        .orElseThrow(CustomLoginIdSigninFailedException::new);
    if (!passwordEncoder.matches(password, memberEntity.getPassword())) {
      throw new CustomLoginIdSigninFailedException();
    }
    return responseService.getSingleResult(
        jwtTokenProvider.createToken(String.valueOf(memberEntity.getId()),
            memberEntity.getRoles()));

  }
}
