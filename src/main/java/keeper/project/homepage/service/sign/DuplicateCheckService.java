package keeper.project.homepage.service.sign;

import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DuplicateCheckService {

  private final MemberRepository memberRepository;

  public boolean checkLoginIdDuplicate(String loginId) {
    return memberRepository.existsByLoginId(loginId);
  }

  public boolean checkEmailAddressDuplicate(String emailAddress) {
    return memberRepository.existsByEmailAddress(emailAddress);
  }

  public boolean checkStudentIdDuplicate(String studentId) {
    return memberRepository.existsByStudentId(studentId);
  }

}
