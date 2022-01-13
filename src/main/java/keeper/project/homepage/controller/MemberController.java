package keeper.project.homepage.controller;

import keeper.project.homepage.config.security.JwtTokenProvider;
import keeper.project.homepage.dto.ListResult;
import keeper.project.homepage.dto.SingleResult;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.MemberRepository;
import keeper.project.homepage.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Secured("ROLE_USER") // 모든 url에 공통 설정
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class MemberController {

  private final MemberRepository memberRepository;
  private final ResponseService responseService;

  @Secured("ROLE_ADMIN") // 각 리소스별 권한 설정
  @GetMapping(value = "/members")
  public ListResult<MemberEntity> findAllMember() {
    // 결과데이터가 여러건인경우 getListResult를 이용해서 결과를 출력한다.
    return responseService.getListResult(memberRepository.findAll());
  }

  @Secured("ROLE_USER") // 각 리소스별 권한 설정
  @GetMapping(value = "/member")
  public SingleResult<MemberEntity> findMember() {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String id = authentication.getName();
    // 결과데이터가 단일건인경우 getSingleResult를 이용해서 결과를 출력한다.
    return responseService.getSingleResult(
        memberRepository.findByLoginId(id).orElseThrow(CustomMemberNotFoundException::new));
  }
}
