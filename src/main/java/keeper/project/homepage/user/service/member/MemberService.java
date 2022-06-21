package keeper.project.homepage.user.service.member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import keeper.project.homepage.user.dto.member.MultiMemberResponseDto;
import keeper.project.homepage.user.dto.posting.PostingResponseDto;
import keeper.project.homepage.user.dto.member.MemberFollowDto;
import keeper.project.homepage.util.image.preprocessing.ImageCenterCropping;
import keeper.project.homepage.common.dto.sign.EmailAuthDto;
import keeper.project.homepage.user.dto.member.MemberDto;
import keeper.project.homepage.user.dto.member.OtherMemberInfoResult;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.EmailAuthRedisEntity;
import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.sign.CustomAuthenticationEntryPointException;
import keeper.project.homepage.exception.member.CustomMemberDuplicateException;
import keeper.project.homepage.exception.member.CustomMemberEmptyFieldException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.EmailAuthRedisRepository;
import keeper.project.homepage.repository.member.FriendRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.util.image.preprocessing.ImageSize;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import keeper.project.homepage.common.service.mail.MailService;
import keeper.project.homepage.common.service.sign.DuplicateCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberUtilService memberUtilService;
  private final MemberRepository memberRepository;
  private final FriendRepository friendRepository;
  private final EmailAuthRedisRepository emailAuthRedisRepository;

  private final ThumbnailService thumbnailService;
  private final FileService fileService;
  private final MailService mailService;
  private final DuplicateCheckService duplicateCheckService;

  private Integer getFolloweeNumber(MemberEntity member) {
    return member.getFollowee().size();
  }

  private Integer getFollowerNumber(MemberEntity member) {
    return member.getFollower().size();
  }

  public List<OtherMemberInfoResult> getOtherMembers() {
    List<OtherMemberInfoResult> otherMemberInfoResultList = new ArrayList<>();
    List<MemberEntity> memberEntityList = memberRepository.findAll();

    for (MemberEntity memberEntity : memberEntityList) {
      if (memberEntity.getMemberType() != null && memberEntity.getMemberType().getId() == 5) {
        continue;
      }
      if (memberEntity.getId().equals(memberUtilService.VIRTUAL_MEMBER_ID)) {
        continue;
      }
      OtherMemberInfoResult otherMemberInfoResult = new OtherMemberInfoResult(memberEntity);
      otherMemberInfoResult.setCheckFollow(memberUtilService.isMyFollowee(memberEntity), memberUtilService.isMyFollower(memberEntity));
      otherMemberInfoResultList.add(otherMemberInfoResult);
    }

    return otherMemberInfoResultList;
  }

  public OtherMemberInfoResult getOtherMember(Long otherMemberId) {
    memberUtilService.checkVirtualMember(otherMemberId);

    MemberEntity other = memberUtilService.getById(otherMemberId);

    OtherMemberInfoResult result = new OtherMemberInfoResult(other);
    result.setCheckFollow(memberUtilService.isMyFollowee(other), memberUtilService.isMyFollower(other));
    return result;
  }

  public List<MultiMemberResponseDto> getMultiMembers(List<Long> ids) {
    List<MultiMemberResponseDto> multiMemberResponseDtos = new ArrayList<>();

    for (Long id : ids) {
      Optional<MemberEntity> member = memberRepository.findById(id);
      if (member.isPresent()) {
        if (member.get().getId().equals(memberUtilService.VIRTUAL_MEMBER_ID)) {
          multiMemberResponseDtos.add(
              MultiMemberResponseDto.builder().id(id).msg("Fail: Access Virtual Member").build());
        } else {
          multiMemberResponseDtos.add(member.get().toMultiMemberResponseDto());
        }
      } else {
        multiMemberResponseDtos.add(
            MultiMemberResponseDto.builder().id(id).msg("Fail: Not Exist Member").build());
      }
    }

    return multiMemberResponseDtos;
  }

  public MemberDto getMember(Long id) {
    MemberEntity memberEntity = memberRepository.findById(id)
        .orElseThrow(CustomMemberNotFoundException::new);

    return new MemberDto(memberEntity);
  }

  public MemberDto updateProfile(MemberDto memberDto, Long memberId) {
    MemberEntity updateEntity = memberRepository.findById(memberId)
        .orElseThrow(CustomMemberNotFoundException::new);
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

  public MemberDto updateEmailAddress(MemberDto memberDto, Long memberId) throws RuntimeException {
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
        .orElseThrow(CustomMemberNotFoundException::new);
    updateEntity.changeEmailAddress(memberDto.getEmailAddress());
    memberDto.initWithEntity(memberRepository.save(updateEntity));
    return memberDto;
  }

  public MemberDto updateThumbnails(Long memberId, MultipartFile image, String ipAddress) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(CustomMemberNotFoundException::new);

    ThumbnailEntity prevThumbnail = memberEntity.getThumbnail();

    ThumbnailEntity thumbnailEntity = thumbnailService.save(ThumbType.MemberThumbnail,
        new ImageCenterCropping(ImageSize.LARGE), image, ipAddress);

    memberEntity.changeThumbnail(thumbnailEntity);
    MemberDto result = new MemberDto();
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
        () -> new CustomMemberNotFoundException(id.toString() + "인 id를 가진 member가 존재하지 않습니다."));

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

  public List<MemberDto> showFollower(Long myId) {
    MemberEntity me = memberUtilService.getById(myId);
    List<FriendEntity> friendList = me.getFollower();

    List<MemberDto> followerList = new ArrayList<>();
    for (FriendEntity friend : friendList) {
      MemberDto follower = MemberDto.builder().build();
      follower.initWithEntity(friend.getFollower());
      followerList.add(follower);
    }
    return followerList;
  }

  public List<MemberDto> showFollowee(Long myId) {
    MemberEntity me = memberUtilService.getById(myId);
    List<FriendEntity> friendList = me.getFollowee();

    List<MemberDto> followeeList = new ArrayList<>();
    for (FriendEntity friend : friendList) {
      MemberDto followee = MemberDto.builder().build();
      followee.initWithEntity(friend.getFollowee());
      followeeList.add(followee);
    }
    return followeeList;
  }

  public MemberFollowDto getFollowerAndFolloweeNumber(Long id) {
    MemberEntity member = memberUtilService.getById(id);
    return MemberFollowDto.builder()
        .followeeNumber(getFolloweeNumber(member))
        .followerNumber(getFollowerNumber(member))
        .build();
  }

}