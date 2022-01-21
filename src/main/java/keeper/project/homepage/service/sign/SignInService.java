package keeper.project.homepage.service.sign;

import javax.transaction.Transactional;
import keeper.project.homepage.config.security.JwtTokenProvider;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomLoginIdSigninFailedException;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Log4j2
public class SignInService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final CustomPasswordService customPasswordService;
  private final JwtTokenProvider jwtTokenProvider;

  public MemberEntity login(String loginId, String password) {
    MemberEntity memberEntity = memberRepository.findByLoginId(loginId)
        .orElseThrow(CustomLoginIdSigninFailedException::new);
    String hashedPassword = memberEntity.getPassword();
    if (!passwordMatches(password, hashedPassword)) {
      throw new CustomLoginIdSigninFailedException();
    }
    return memberEntity;
  }

  private boolean passwordMatches(String password, String hashedPassword) {
    return passwordEncoder.matches(password, hashedPassword)
        || customPasswordService.checkPasswordWithPBKDF2SHA256(password, hashedPassword)
        || customPasswordService.checkPasswordWithMD5(password, hashedPassword);
  }

  public String createJwtToken(MemberEntity memberEntity) {
    return "Bearer " + jwtTokenProvider.createToken(String.valueOf(memberEntity.getId()),
        memberEntity.getRoles());
  }

  @Transactional
  public void changePassword(String newPassword) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer id = getIdFromAuth(authentication);
    MemberEntity memberEntity = memberRepository.findById(id)
        .orElseThrow(CustomLoginIdSigninFailedException::new);
    memberEntity.changePassword(passwordEncoder.encode(newPassword));
    memberRepository.save(memberEntity);
  }

  private Integer getIdFromAuth(Authentication authentication) {
    int id;
    try {
      id = Integer.parseInt(authentication.getName());
    } catch (NumberFormatException e) {
      throw new CustomLoginIdSigninFailedException("잘못된 JWT 토큰입니다.");
    }
    return id;
  }
}
