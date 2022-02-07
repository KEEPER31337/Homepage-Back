package keeper.project.homepage.service.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import keeper.project.homepage.common.ImageCenterCrop;
import keeper.project.homepage.dto.EmailAuthDto;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.dto.member.MemberJobDto;
import keeper.project.homepage.dto.member.MemberRankDto;
import keeper.project.homepage.dto.member.MemberTypeDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.EmailAuthRedisEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.exception.CustomAuthenticationEntryPointException;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.exception.CustomSignUpFailedException;
import keeper.project.homepage.repository.member.EmailAuthRedisRepository;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRankRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import keeper.project.homepage.service.FileService;
import keeper.project.homepage.service.ThumbnailService;
import keeper.project.homepage.service.mail.MailService;
import keeper.project.homepage.service.sign.DuplicateCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

  private static final int AUTH_CODE_LENGTH = 10;

  private final MemberRepository memberRepository;
  private final EmailAuthRedisRepository emailAuthRedisRepository;
  private final MemberRankRepository memberRankRepository;
  private final MemberTypeRepository memberTypeRepository;
  private final MemberJobRepository memberJobRepository;
  private final MemberHasMemberJobRepository memberHasMemberJobRepository;

  private final ThumbnailService thumbnailService;
  private final FileService fileService;
  private final MailService mailService;
  private final DuplicateCheckService duplicateCheckService;

  public MemberEntity findById(Long id) throws RuntimeException {
    return memberRepository.findById(id).orElseThrow(CustomMemberNotFoundException::new);
  }

  public List<MemberEntity> findAll() {
    return memberRepository.findAll();
  }

  public MemberDto updateMemberRank(MemberRankDto rankDto, String loginId) {
    if (rankDto.getName().isBlank()) {
      throw new RuntimeException("변경할 등급을 입력해주세요.");
    }

    MemberEntity updateEntity = memberRepository.findByLoginId(loginId)
        .orElseThrow(CustomMemberNotFoundException::new);
    MemberRankEntity prevRank = updateEntity.getMemberRank();
    if (prevRank != null) {
      prevRank.getMembers().remove(updateEntity);
    }

    MemberRankEntity updateRank = memberRankRepository.findByName(rankDto.getName())
        .orElse(null);
    if (updateRank == null) { // 나중에 custom exception 으로 변경
      throw new RuntimeException(rankDto.getName() + "인 member rank가 존재하지 않습니다.");
    }
    updateRank.getMembers().add(updateEntity);
    updateEntity.changeMemberRank(updateRank);
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateEntity));
    return result;
  }

  public MemberDto updateMemberType(MemberTypeDto typeDto, String loginId) {
    if (typeDto.getName().isBlank()) {
      throw new RuntimeException("변경할 타입을 입력해주세요.");
    }

    MemberEntity updateEntity = memberRepository.findByLoginId(loginId)
        .orElseThrow(CustomMemberNotFoundException::new);
    MemberTypeEntity prevType = updateEntity.getMemberType();
    if (prevType != null) {
      prevType.getMembers().remove(updateEntity);
    }

    MemberTypeEntity updateType = memberTypeRepository.findByName(typeDto.getName())
        .orElse(null);
    if (updateType == null) { // 나중에 custom exception 으로 변경
      throw new RuntimeException(typeDto.getName() + "인 member type이 존재하지 않습니다.");
    }
    updateType.getMembers().add(updateEntity);
    updateEntity.changeMemberType(updateType);
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateEntity));
    return result;
  }

  private MemberEntity removeMemberJob(MemberHasMemberJobEntity mj, MemberEntity member) {
    memberHasMemberJobRepository.delete(mj);
    mj.getMemberJobEntity().getMembers().remove(mj);
    member.getMemberJobs().remove(mj);
    return member;
  }

  private MemberEntity addMemberJob(String jobName, MemberEntity member) {
    MemberJobEntity newJob = memberJobRepository.findByName(jobName).orElse(null);
    if (newJob == null) { // 나중에 custom exception 으로 변경
      throw new RuntimeException(jobName + "인 member job이 존재하지 않습니다.");
    }

    MemberHasMemberJobEntity newMJ = memberHasMemberJobRepository.save(
        MemberHasMemberJobEntity.builder().memberEntity(member)
            .memberJobEntity(newJob).build());
    newJob.getMembers().add(newMJ);
    member.getMemberJobs().add(newMJ);
    return member;
  }

  public MemberDto updateMemberJobs(MemberJobDto jobDto, String loginId) {
    if (jobDto.getNames().isEmpty()) {
      throw new RuntimeException("변경할 타입을 입력해주세요.");
    }

    MemberEntity updateMember = memberRepository.findByLoginId(loginId)
        .orElseThrow(CustomMemberNotFoundException::new);

    List<MemberHasMemberJobEntity> prevMJList = memberHasMemberJobRepository.findAllByMemberEntity_Id(
        updateMember.getId());
    if (!prevMJList.isEmpty()) {
      for (MemberHasMemberJobEntity prevMJ : prevMJList) {
        updateMember = removeMemberJob(prevMJ, updateMember);
      }
    }

    for (String name : jobDto.getNames()) {
      updateMember = addMemberJob(name, updateMember);
    }
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateMember));
    return result;
  }

  // update realName, nickName
  public MemberDto updateNames(MemberDto memberDto, Long memberId) {
    MemberEntity updateEntity = memberRepository.findById(memberId)
        .orElseThrow(CustomMemberNotFoundException::new);
    if (memberDto.getRealName().isBlank()) {
      throw new RuntimeException("변경할 이름을 입력해주세요.");
    }
    if (memberDto.getNickName().isBlank()) {
      throw new RuntimeException("변경할 닉네임을 입력해주세요.");
    }
    updateEntity.changeRealName(memberDto.getRealName());
    updateEntity.changeNickName(memberDto.getNickName());
    memberDto.initWithEntity(memberRepository.save(updateEntity));
    return memberDto;
  }

  public MemberDto updateStudentId(MemberDto memberDto, Long memberId) {
    MemberEntity updateEntity = memberRepository.findById(memberId)
        .orElseThrow(CustomMemberNotFoundException::new);
    if (memberDto.getStudentId().isBlank()) {
      throw new RuntimeException("변경할 학번을 입력해주세요.");
    }
    if (duplicateCheckService.checkStudentIdDuplicate(memberDto.getStudentId())) {
      throw new RuntimeException("이미 사용중인 학번입니다.");
    }
    updateEntity.changeStudentId(memberDto.getStudentId());
    memberDto.initWithEntity(memberRepository.save(updateEntity));
    return memberDto;
  }

  //TODO
  // Signup service와 중복되는 메소드, 리팩토링 필요
  public EmailAuthDto generateEmailAuth(EmailAuthDto emailAuthDto) {
    String generatedAuthCode = generateRandomAuthCode(AUTH_CODE_LENGTH);
    emailAuthDto.setAuthCode(generatedAuthCode);
    emailAuthRedisRepository.save(
        new EmailAuthRedisEntity(emailAuthDto.getEmailAddress(), emailAuthDto.getAuthCode()));
    return emailAuthDto;
  }

  //TODO
  // Signup service와 중복되는 메소드, 리팩토링 필요
  public void sendEmailAuthCode(EmailAuthDto emailAuthDto) {
    List<String> toUserList = new ArrayList<>(List.of(emailAuthDto.getEmailAddress()));
    String subject = "KEEPER 인증코드 발송 메일입니다.";
    String text = "KEEPER 인증코드는 " + emailAuthDto.getAuthCode() + " 입니다.";
    mailService.sendMail(toUserList, subject, text);
  }

  public MemberDto updateEmailAddress(MemberDto memberDto, Long memberId) throws RuntimeException {
    if (memberDto.getEmailAddress().isBlank()) {
      throw new RuntimeException("변경할 이메일을 입력해주세요.");
    }
    if (duplicateCheckService.checkEmailAddressDuplicate(memberDto.getEmailAddress())) {
      throw new RuntimeException("이미 사용중인 이메일 입니다.");
    }
    String memberEmail = memberDto.getEmailAddress();
    String authCode = memberDto.getAuthCode();

    Optional<EmailAuthRedisEntity> getEmailAuthRedisEntity = emailAuthRedisRepository.findById(
        memberEmail);
    if (getEmailAuthRedisEntity.isEmpty()) {
      throw new CustomAuthenticationEntryPointException("이메일 인증 코드가 만료되었습니다.");
    }
    if (!authCode.equals(getEmailAuthRedisEntity.get().getAuthCode())) {
      throw new CustomAuthenticationEntryPointException("이메일 인증 코드가 일치하지 않습니다.");
    }

    MemberEntity updateEntity = memberRepository.findById(memberId)
        .orElseThrow(CustomMemberNotFoundException::new);
    updateEntity.changeEmailAddress(memberDto.getEmailAddress());
    memberDto.initWithEntity(memberRepository.save(updateEntity));
    return memberDto;
  }

  public MemberDto updateThumbnails(Long memberId, MultipartFile image, String ipAddress) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(CustomMemberNotFoundException::new);

    if (memberEntity.getThumbnail() != null) {
      ThumbnailEntity prevThumbnail = thumbnailService.findById(
          memberEntity.getThumbnail().getId());
      fileService.deleteById(prevThumbnail.getFile().getId());
      thumbnailService.deleteById(prevThumbnail.getId());
    }

    FileEntity fileEntity = fileService.saveOriginalImage(image, ipAddress);
    ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
        image, fileEntity, "small");

    memberEntity.changeThumbnail(thumbnailEntity);
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(memberEntity));
    return result;
  }

  //TODO
  // Signup service와 중복되는 메소드, 리팩토링 필요
  private String generateRandomAuthCode(int targetStringLength) {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    Random random = new Random();

    return random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    // 출처: https://www.baeldung.com/java-random-string
  }
}
