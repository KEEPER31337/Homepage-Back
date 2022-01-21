package keeper.project.homepage.service.sign;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRankRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SignUpService {

  private final MemberRepository memberRepository;
  private final MemberTypeRepository memberTypeRepository;
  private final MemberRankRepository memberRankRepository;
  private final PasswordEncoder passwordEncoder;

  public void signUp(String loginId, String emailAddress, String password, String realName,
      String nickName, Date birthday, String studentId) {
    memberRepository.save(MemberEntity.builder()
        .loginId(loginId)
        .emailAddress(emailAddress)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .birthday(birthday)
        .studentId(studentId)
        .memberType(memberTypeRepository.getById(1))
        .memberRank(memberRankRepository.getById(1))
        .roles(new ArrayList<String>(List.of("ROLE_USER")))
        .build());
  }
}
