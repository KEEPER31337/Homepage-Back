package keeper.project.homepage.member.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import keeper.project.homepage.member.dto.response.MemberFollowResponseDto;
import keeper.project.homepage.member.dto.response.UserMemberResponseDto;
import keeper.project.homepage.member.entity.EmailAuthRedisEntity;
import keeper.project.homepage.member.entity.FriendEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.exception.CustomMemberDuplicateException;
import keeper.project.homepage.member.exception.CustomMemberEmptyFieldException;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.member.repository.FriendRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.posting.dto.PostingResponseDto;
import keeper.project.homepage.sign.dto.EmailAuthDto;
import keeper.project.homepage.sign.exception.CustomAuthenticationEntryPointException;
import keeper.project.homepage.sign.repository.EmailAuthRedisRepository;
import keeper.project.homepage.sign.service.DuplicateCheckService;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.util.image.preprocessing.ImageCenterCropping;
import keeper.project.homepage.util.image.preprocessing.ImageSize;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import keeper.project.homepage.util.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final FriendRepository friendRepository;
  private final EmailAuthRedisRepository emailAuthRedisRepository;

  private final MemberUtilService memberUtilService;
  private final ThumbnailService thumbnailService;
  private final MailService mailService;
  private final DuplicateCheckService duplicateCheckService;

  private Integer getFolloweeNumber(MemberEntity member) {
    return member.getFollowee().size();
  }

  private Integer getFollowerNumber(MemberEntity member) {
    return member.getFollower().size();
  }

  public UserMemberResponseDto updateProfile(UserMemberResponseDto memberDto, Long memberId) {
    MemberEntity updateEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomMemberNotFoundException(memberId));
    if (memberDto.getRealName().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 이름의 내용이 비어있습니다.");
    }
    if (memberDto.getNickName().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 닉네임의 내용이 비어있습니다.");
    }
    if (memberDto.getStudentId().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 학번의 내용이 비어있습니다.");
    }
    if (duplicateCheckService.isStudentIdDuplicate(memberDto.getStudentId())) {
      throw new CustomMemberDuplicateException("이미 사용중인 학번입니다.");
    }
    updateEntity.changeRealName(memberDto.getRealName());
    updateEntity.changeNickName(memberDto.getNickName());
    updateEntity.changeStudentId(memberDto.getStudentId());
    memberDto.initWithEntity(memberRepository.save(updateEntity));
    return memberDto;
  }

  //TODO
  // Signup service와 중복되는 메소드, 리팩토링 필요
  public EmailAuthDto generateEmailAuth(EmailAuthDto emailAuthDto) {
    String generatedAuthCode = generateRandomAuthCode(memberUtilService.EMAIL_AUTH_CODE_LENGTH);
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

  public UserMemberResponseDto updateEmailAddress(UserMemberResponseDto memberDto, Long memberId)
      throws RuntimeException {
    if (memberDto.getEmailAddress().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 이메일의 내용이 비어있습니다.");
    }
    if (duplicateCheckService.isEmailAddressDuplicate(memberDto.getEmailAddress())) {
      throw new CustomMemberDuplicateException("이미 사용중인 이메일 입니다.");
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
        .orElseThrow(() -> new CustomMemberNotFoundException(memberId));
    updateEntity.changeEmailAddress(memberDto.getEmailAddress());
    memberDto.initWithEntity(memberRepository.save(updateEntity));
    return memberDto;
  }

  public UserMemberResponseDto updateThumbnails(Long memberId, MultipartFile image, String ipAddress) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomMemberNotFoundException(memberId));

    ThumbnailEntity prevThumbnail = memberEntity.getThumbnail();

    ThumbnailEntity thumbnailEntity = thumbnailService.save(ThumbType.MemberThumbnail,
        new ImageCenterCropping(ImageSize.LARGE), image, ipAddress);

    memberEntity.changeThumbnail(thumbnailEntity);
    UserMemberResponseDto result = new UserMemberResponseDto();
    result.initWithEntity(memberRepository.save(memberEntity));

    if (prevThumbnail != null) {
      thumbnailService.delete(prevThumbnail.getId());
    }
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

  public Page<PostingResponseDto> findAllPostingByIsTemp(Long id, Pageable pageable,
      Integer isTemp) {
    MemberEntity memberEntity = memberRepository.findById(id).orElseThrow(
        () -> new CustomMemberNotFoundException(id));

    List<PostingResponseDto> postings = new ArrayList<>();
    PostingResponseDto postingResponseDto = new PostingResponseDto();
    Integer postingSize = memberEntity.getPosting().size();
    memberEntity.getPosting().forEach(posting -> {
      if (posting.getIsTemp() == isTemp) {
        PostingResponseDto dto = new PostingResponseDto(posting, postingSize, false);
        postings.add(dto);
      }
    });
    final int start = (int) pageable.getOffset();
    final int end = Math.min((start + pageable.getPageSize()), postings.size());
    final Page<PostingResponseDto> page = new PageImpl<>(postings.subList(start, end), pageable,
        postings.size());

    return page;
  }

  public void follow(Long myId, Long memberId) {
    MemberEntity me = memberUtilService.getById(myId);
    MemberEntity followee = memberUtilService.getById(memberId);

    FriendEntity friend = FriendEntity.builder()
        .follower(me)
        .followee(followee)
        .registerDate(LocalDate.now())
        .build();
    friendRepository.save(friend);

    me.getFollowee().add(friend);
    followee.getFollower().add(friend);
  }

  public void unfollow(Long myId, Long memberId) {
    MemberEntity me = memberUtilService.getById(myId);
    MemberEntity followee = memberUtilService.getById(memberId);

    FriendEntity friend = friendRepository.findByFolloweeAndFollower(followee, me);
    me.getFollowee().remove(friend);
    followee.getFollower().remove(friend);
    friendRepository.delete(friend);
  }

  public List<UserMemberResponseDto> showFollower(Long myId) {
    MemberEntity me = memberUtilService.getById(myId);
    List<FriendEntity> friendList = me.getFollower();

    List<UserMemberResponseDto> followerList = new ArrayList<>();
    for (FriendEntity friend : friendList) {
      UserMemberResponseDto follower = UserMemberResponseDto.builder().build();
      follower.initWithEntity(friend.getFollower());
      followerList.add(follower);
    }
    return followerList;
  }

  public List<UserMemberResponseDto> showFollowee(Long myId) {
    MemberEntity me = memberUtilService.getById(myId);
    List<FriendEntity> friendList = me.getFollowee();

    List<UserMemberResponseDto> followeeList = new ArrayList<>();
    for (FriendEntity friend : friendList) {
      UserMemberResponseDto followee = UserMemberResponseDto.builder().build();
      followee.initWithEntity(friend.getFollowee());
      followeeList.add(followee);
    }
    return followeeList;
  }

  public MemberFollowResponseDto getFollowerAndFolloweeNumber(Long id) {
    MemberEntity member = memberUtilService.getById(id);
    return MemberFollowResponseDto.builder()
        .followeeNumber(getFolloweeNumber(member))
        .followerNumber(getFollowerNumber(member))
        .build();
  }

  public List<Float> getAllGenerations() {
    return memberRepository.findDistinctGenerationOrderByGenerationDesc();
  }
}