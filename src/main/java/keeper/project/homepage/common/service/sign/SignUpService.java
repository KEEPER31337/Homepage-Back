package keeper.project.homepage.common.service.sign;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import keeper.project.homepage.common.dto.sign.EmailAuthDto;
import keeper.project.homepage.user.dto.member.MemberDto;
import keeper.project.homepage.entity.member.EmailAuthRedisEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.exception.sign.CustomSignUpFailedException;
import keeper.project.homepage.repository.member.EmailAuthRedisRepository;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRankRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import keeper.project.homepage.common.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class SignUpService {

  private static final int AUTH_CODE_LENGTH = 10;
  public final static Integer KEEPER_FOUNDING_YEAR = 2009;
  public final static Integer HALF_GENERATION_MONTH = 7;

  private final MemberRepository memberRepository;
  private final MemberHasMemberJobRepository hasMemberJobRepository;
  private final MemberJobRepository memberJobRepository;
  private final MemberTypeRepository memberTypeRepository;
  private final MemberRankRepository memberRankRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailAuthRedisRepository emailAuthRedisRepository;
  private final MailService mailService;
  private final DuplicateCheckService duplicateCheckService;

  public EmailAuthDto generateEmailAuth(EmailAuthDto emailAuthDto) {
    String generatedAuthCode = generateRandomAuthCode(AUTH_CODE_LENGTH);
    emailAuthDto.setAuthCode(generatedAuthCode);
    emailAuthRedisRepository.save(
        new EmailAuthRedisEntity(emailAuthDto.getEmailAddress(), emailAuthDto.getAuthCode()));
    return emailAuthDto;
  }

  public void sendEmailAuthCode(EmailAuthDto emailAuthDto) {
    List<String> toUserList = new ArrayList<>(List.of(emailAuthDto.getEmailAddress()));
    String subject = "KEEPER ???????????? ?????? ???????????????.";
    String text = "KEEPER ??????????????? " + emailAuthDto.getAuthCode() + " ?????????.";
    mailService.sendMail(toUserList, subject, text);
  }

  public void signUpWithEmailAuthCode(MemberDto memberDto) {
    String memberEmail = memberDto.getEmailAddress();
    String authCode = memberDto.getAuthCode();

    Optional<EmailAuthRedisEntity> getEmailAuthRedisEntity = emailAuthRedisRepository.findById(
        memberEmail);
    if (getEmailAuthRedisEntity.isEmpty()) {
      throw new CustomSignUpFailedException("????????? ?????? ????????? ?????????????????????.");
    }
    if (!authCode.equals(getEmailAuthRedisEntity.get().getAuthCode())) {
      throw new CustomSignUpFailedException("????????? ?????? ????????? ???????????? ????????????.");
    }

    if (!isValidAll(memberDto)) {
      throw new CustomSignUpFailedException("????????? ?????? ??????");
    }
    MemberEntity memberEntity = MemberEntity.builder()
        .loginId(memberDto.getLoginId())
        .emailAddress(memberDto.getEmailAddress())
        .password(passwordEncoder.encode(memberDto.getPassword()))
        .realName(memberDto.getRealName())
        .nickName(memberDto.getNickName())
        .birthday(memberDto.getBirthday())
        .studentId(memberDto.getStudentId())
        .memberType(memberTypeRepository.getById(1L))
        .memberRank(memberRankRepository.getById(1L))
        .generation(getMemberGeneration())
        .build();
    memberRepository.save(memberEntity);

    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_??????").get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberEntity(memberEntity)
        .memberJobEntity(memberJobEntity)
        .build();
    hasMemberJobRepository.save(hasMemberJobEntity);
  }

  private Float getMemberGeneration() {
    LocalDate date = LocalDate.now();
    Float generation = (float) (date.getYear() - KEEPER_FOUNDING_YEAR);
    if (date.getMonthValue() >= HALF_GENERATION_MONTH) {
      generation += 0.5F;
    }
    return generation;
  }

  private String generateRandomAuthCode(int targetStringLength) {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    Random random = new Random();

    return random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    // ??????: https://www.baeldung.com/java-random-string
  }

  private boolean isValidAll(MemberDto memberDto) {
    return !isDuplicate(memberDto) && isValid(memberDto);
  }

  private boolean isDuplicate(MemberDto memberDto) {
    return duplicateCheckService.isEmailAddressDuplicate(memberDto.getEmailAddress()) ||
        duplicateCheckService.isLoginIdDuplicate(memberDto.getLoginId()) ||
        duplicateCheckService.isStudentIdDuplicate(memberDto.getStudentId());
  }

  private boolean isValid(MemberDto memberDto) {
    return isLoginIdValid(memberDto.getLoginId()) &&
        isPasswordValid(memberDto.getPassword()) &&
        isNicknameValid(memberDto.getNickName()) &&
        isRealnameValid(memberDto.getRealName()) &&
        isEmailValid(memberDto.getEmailAddress()) &&
        isStudentIdValid(memberDto.getStudentId());
  }

  private boolean hasSpecialCharacter(String val) {
    String pattern = "^[???-???|a-z|A-Z|0-9|_|]+$"; // ??????, a-z, A-Z, 0-9, '_'??? ????????? ?????? ????????????
    if (!Pattern.matches(pattern, val)) {
      return true;
    }
    return false;
  }

  private boolean isLoginIdValid(String loginId) {

    if (hasSpecialCharacter(loginId)) {
      log.info("loginId: '" + loginId + "'?????? ??????????????? ????????? ??? ????????????.");
      return false;
    }
    String pattern = "^[a-zA-Z\\d_]{4,12}$"; // 4 ~ 12??? ??????, ??????, '_' ??????
    if (!Pattern.matches(pattern, loginId)) {
      log.info("????????? ????????? ?????????????????????. loginId: " + loginId);
      return false;
    }
    return true;
  }

  private boolean isPasswordValid(String password) {

    String pattern = "^(?=.*[a-zA-Z])(?=.*\\d).{8,20}$"; // 8??? ?????? ??????, ?????? ?????? ??????
    if (!Pattern.matches(pattern, password)) {
      log.info("????????? ????????? ?????????????????????. password: " + password);
      return false;
    }
    return true;
  }

  private boolean isNicknameValid(String nickname) {

    if (hasSpecialCharacter(nickname)) {
      log.info("nickname: '" + nickname + "'?????? ??????????????? ????????? ??? ????????????.");
      return false;
    }
    String pattern = "^[a-zA-Z???-???0-9].{0,16}$"; // 1~16??? ??????, ??????, ?????? ??????
    if (!Pattern.matches(pattern, nickname)) {
      log.info("????????? ????????? ?????????????????????. nickname: " + nickname);
      return false;
    }
    return true;
  }

  private boolean isRealnameValid(String realname) {

    if (hasSpecialCharacter(realname)) {
      log.info("realname: '" + realname + "'?????? ??????????????? ????????? ??? ????????????.");
      return false;
    }
    String pattern = "^[a-zA-Z???-???].{0,20}$"; // 1~20??? ??????, ?????? ??????
    if (!Pattern.matches(pattern, realname)) {
      log.info("????????? ????????? ?????????????????????. realname: " + realname);
      return false;
    }
    return true;
  }

  private boolean isEmailValid(String emailAddress) {

    String pattern = "\\w+@\\w+\\.\\w+(\\.\\w+)?"; //Email ???????????? ?????????
    if (!Pattern.matches(pattern, emailAddress)) {
      log.info("????????? ????????? ?????????????????????. emailAddress: " + emailAddress);
      return false;
    }
    return true;
  }

  private boolean isStudentIdValid(String studentId) {

    if (hasSpecialCharacter(studentId)) {
      log.info("studentId: '" + studentId + "'?????? ??????????????? ????????? ??? ????????????.");
      return false;
    }
    String pattern = "^[0-9]*$"; // ?????? ???????????? ?????????
    if (!Pattern.matches(pattern, studentId)) {
      log.info("????????? ????????? ?????????????????????. studentId: " + studentId);
      return false;
    }
    return true;
  }
}
