package keeper.project.homepage.common.service.sign;

import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DuplicateCheckService {

  private final MemberRepository memberRepository;

  public boolean isLoginIdDuplicate(String loginId) {
    return memberRepository.existsByLoginId(loginId);
  }

  public boolean isEmailAddressDuplicate(String emailAddress) {
    return memberRepository.existsByEmailAddress(emailAddress);
  }

  public boolean isStudentIdDuplicate(String studentId) {
    return memberRepository.existsByStudentId(studentId);
  }

}
