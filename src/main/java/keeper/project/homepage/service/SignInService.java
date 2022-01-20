package keeper.project.homepage.service;

import keeper.project.homepage.config.security.JwtTokenProvider;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.exception.CustomLoginIdSigninFailedException;
import keeper.project.homepage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
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
    return passwordEncoder.matches(password, hashedPassword) ||
        customPasswordService.checkPasswordWithPBKDF2SHA256(password, hashedPassword) ||
        customPasswordService.checkPasswordWithMD5(password, hashedPassword);
  }

  public String createJwtToken(MemberEntity memberEntity) {
    return jwtTokenProvider.createToken(String.valueOf(memberEntity.getId()),
        memberEntity.getRoles());
  }
}
