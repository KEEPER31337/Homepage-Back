package keeper.project.homepage.common.service.sign;

import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class DuplicateCheckService {

  private final MemberRepository memberRepository;

  public boolean isLoginIdDuplicate(String loginId) {
    if (memberRepository.existsByLoginId(loginId)) {
      log.info("LoginId: '" + loginId + "' is duplicate.");
      return true;
    }
    return false;
  }

  public boolean isEmailAddressDuplicate(String emailAddress) {
    if (memberRepository.existsByEmailAddress(emailAddress)) {
      log.info("EmailAddress: '" + emailAddress + "' is duplicate.");
      return true;
    }
    return false;
  }

  public boolean isStudentIdDuplicate(String studentId) {
    if (memberRepository.existsByStudentId(studentId)) {
      log.info("StudentId: '" + studentId + "' is duplicate.");
      return true;
    }
    return false;
  }

}
