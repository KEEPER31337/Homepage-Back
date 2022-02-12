package keeper.project.homepage.service.member;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.dto.result.OtherMemberInfoResult;
import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.dto.request.PointTransferRequest;
import keeper.project.homepage.dto.result.PointTransferResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasCommentDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasCommentLikeEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.exception.member.CustomMemberDuplicateException;
import keeper.project.homepage.exception.member.CustomMemberEmptyFieldException;
import keeper.project.homepage.exception.member.CustomMemberInfoNotFoundException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.exception.CustomTransferPointLackException;
import keeper.project.homepage.repository.member.FriendRepository;
import java.util.ArrayList;
import java.util.Random;
import keeper.project.homepage.common.ImageCenterCrop;
import keeper.project.homepage.dto.EmailAuthDto;
import keeper.project.homepage.dto.member.MemberJobDto;
import keeper.project.homepage.dto.member.MemberRankDto;
import keeper.project.homepage.dto.member.MemberTypeDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.EmailAuthRedisEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.exception.CustomAuthenticationEntryPointException;
import keeper.project.homepage.repository.member.EmailAuthRedisRepository;
import keeper.project.homepage.repository.member.MemberHasCommentDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasCommentLikeRepository;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRankRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import keeper.project.homepage.repository.posting.CommentRepository;
import keeper.project.homepage.service.FileService;
import keeper.project.homepage.service.ThumbnailService;
import keeper.project.homepage.service.mail.MailService;
import keeper.project.homepage.service.posting.CommentService;
import keeper.project.homepage.service.sign.DuplicateCheckService;
import lombok.RequiredArgsConstructor;
import keeper.project.homepage.dto.posting.PostingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

  private static final int AUTH_CODE_LENGTH = 10;

  private final MemberRepository memberRepository;
  private final FriendRepository friendRepository;
  private final EmailAuthRedisRepository emailAuthRedisRepository;
  private final MemberRankRepository memberRankRepository;
  private final MemberTypeRepository memberTypeRepository;
  private final MemberJobRepository memberJobRepository;
  private final MemberHasMemberJobRepository memberHasMemberJobRepository;
  private final CommentRepository commentRepository;
  private final MemberHasCommentLikeRepository memberHasCommentLikeRepository;
  private final MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;

  private final ThumbnailService thumbnailService;
  private final FileService fileService;
  private final MailService mailService;
  private final DuplicateCheckService duplicateCheckService;


  public MemberEntity findById(Long id) {
    return memberRepository.findById(id).orElseThrow(CustomMemberNotFoundException::new);
  }

  public MemberEntity findByLoginId(String loginId) {
    return memberRepository.findByLoginId(loginId).orElseThrow(CustomMemberNotFoundException::new);
  }

  private MemberRankEntity findRankByRankName(String name) {
    return memberRankRepository.findByName(name).orElseThrow(
        () -> new CustomMemberInfoNotFoundException(name + "인 MemberRankEntity가 존재하지 않습니다."));
  }

  private MemberTypeEntity findTypeByTypeName(String name) {
    return memberTypeRepository.findByName(name).orElseThrow(
        () -> new CustomMemberInfoNotFoundException(name + "인 MemberTypeEntity가 존재하지 않습니다."));
  }

  private MemberJobEntity findJobByJobName(String name) {
    return memberJobRepository.findByName(name).orElseThrow(
        () -> new CustomMemberInfoNotFoundException(name + "인 MemberJobEntity가 존재하지 않습니다."));
  }

  public List<MemberEntity> findAll() {
    return memberRepository.findAll();
  }

  public MemberDto updateMemberRank(MemberRankDto rankDto, String loginId) {
    if (rankDto.getName().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 등급의 이름이 비어있습니다.");
    }

    MemberEntity updateEntity = findByLoginId(loginId);
    MemberRankEntity prevRank = updateEntity.getMemberRank();
    if (prevRank != null) {
      prevRank.getMembers().remove(updateEntity);
    }

    MemberRankEntity updateRank = findRankByRankName(rankDto.getName());
    updateRank.getMembers().add(updateEntity);
    updateEntity.changeMemberRank(updateRank);
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateEntity));
    return result;
  }

  public MemberDto updateMemberType(MemberTypeDto typeDto, String loginId) {
    if (typeDto.getName().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 타입의 이름이 비어있습니다.");
    }

    MemberEntity updateEntity = findByLoginId(loginId);
    MemberTypeEntity prevType = updateEntity.getMemberType();
    if (prevType != null) {
      prevType.getMembers().remove(updateEntity);
    }

    MemberTypeEntity updateType = findTypeByTypeName(typeDto.getName());
    updateType.getMembers().add(updateEntity);
    updateEntity.changeMemberType(updateType);
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateEntity));
    return result;
  }

  private MemberEntity deleteMemberJob(MemberHasMemberJobEntity mj, MemberEntity member) {
    memberHasMemberJobRepository.delete(mj);
    mj.getMemberJobEntity().getMembers().remove(mj);
    member.getMemberJobs().remove(mj);
    return member;
  }

  private MemberEntity addMemberJob(String jobName, MemberEntity member) {
    MemberJobEntity newJob = findJobByJobName(jobName);

    MemberHasMemberJobEntity newMJ = memberHasMemberJobRepository.save(
        MemberHasMemberJobEntity.builder()
            .memberEntity(member)
            .memberJobEntity(newJob)
            .build());
    newJob.getMembers().add(newMJ);
    member.getMemberJobs().add(newMJ);
    return member;
  }

  public MemberDto updateMemberJobs(MemberJobDto jobDto, String loginId) {
    if (jobDto.getNames().isEmpty()) {
      throw new CustomMemberEmptyFieldException("변경할 직책의 이름이 비어있습니다.");
    }

    MemberEntity updateMember = findByLoginId(loginId);
    List<MemberHasMemberJobEntity> prevMJList = memberHasMemberJobRepository.findAllByMemberEntity_Id(
        updateMember.getId());
    if (!prevMJList.isEmpty()) {
      for (MemberHasMemberJobEntity prevMJ : prevMJList) {
        updateMember = deleteMemberJob(prevMJ, updateMember);
      }
    }

    for (String name : jobDto.getNames()) {
      updateMember = addMemberJob(name, updateMember);
    }
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(updateMember));
    return result;
  }

  public void follow(Long myId, String followLoginId) {
    MemberEntity me = findById(myId);
    MemberEntity followee = findByLoginId(followLoginId);

    FriendEntity friend = FriendEntity.builder()
        .follower(me)
        .followee(followee)
        .registerDate(LocalDate.now())
        .build();
    friendRepository.save(friend);

    me.getFollowee().add(friend);
    followee.getFollower().add(friend);
  }

  public void unfollow(Long myId, String followLoginId) {
    MemberEntity me = findById(myId);
    MemberEntity followee = findByLoginId(followLoginId);

    FriendEntity friend = friendRepository.findByFolloweeAndFollower(followee, me);
    me.getFollowee().remove(friend);
    followee.getFollower().remove(friend);
    friendRepository.delete(friend);
  }

  public List<MemberDto> showFollower(Long myId) {
    MemberEntity me = findById(myId);
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
    MemberEntity me = findById(myId);
    List<FriendEntity> friendList = me.getFollowee();

    List<MemberDto> followeeList = new ArrayList<>();
    for (FriendEntity friend : friendList) {
      MemberDto followee = MemberDto.builder().build();
      followee.initWithEntity(friend.getFollowee());
      followeeList.add(followee);
    }
    return followeeList;
  }

  // update realName, nickName
  public MemberDto updateNames(MemberDto memberDto, Long memberId) {
    MemberEntity updateEntity = memberRepository.findById(memberId)
        .orElseThrow(CustomMemberNotFoundException::new);
    if (memberDto.getRealName().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 이름의 내용이 비어있습니다.");
    }
    if (memberDto.getNickName().isBlank()) {
      throw new CustomMemberEmptyFieldException("변경할 닉네임의 내용이 비어있습니다.");
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
      throw new CustomMemberEmptyFieldException("변경할 학번의 내용이 비어있습니다.");
    }
    if (duplicateCheckService.checkStudentIdDuplicate(memberDto.getStudentId())) {
      throw new CustomMemberDuplicateException("이미 사용중인 학번입니다.");
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
      throw new CustomMemberEmptyFieldException("변경할 이메일의 내용이 비어있습니다.");
    }
    if (duplicateCheckService.checkEmailAddressDuplicate(memberDto.getEmailAddress())) {
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

    ThumbnailEntity prevThumbnail = null;
    if (memberEntity.getThumbnail() != null) {
      prevThumbnail = thumbnailService.findById(memberEntity.getThumbnail().getId());
    }

    FileEntity fileEntity = fileService.saveOriginalThumbnail(image, ipAddress);
    ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
        image, fileEntity, "small");

    memberEntity.changeThumbnail(thumbnailEntity);
    MemberDto result = new MemberDto();
    result.initWithEntity(memberRepository.save(memberEntity));

    if (prevThumbnail != null) {
      thumbnailService.deleteById(prevThumbnail.getId());
      fileService.deleteOriginalThumbnailById(prevThumbnail.getFile().getId());
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

  public Page<PostingDto> findAllPostingByIsTemp(Long id, Pageable pageable, Integer isTemp) {
    MemberEntity memberEntity = memberRepository.findById(id).orElseThrow(
        () -> new CustomMemberNotFoundException(id.toString() + "인 id를 가진 member가 존재하지 않습니다."));

    List<PostingDto> postings = new ArrayList<>();
    memberEntity.getPosting().forEach(posting -> {
      if (posting.getIsTemp() == isTemp) {
        postings.add(PostingDto.create(posting));
      }
    });
    final int start = (int) pageable.getOffset();
    final int end = Math.min((start + pageable.getPageSize()), postings.size());
    final Page<PostingDto> page = new PageImpl<>(postings.subList(start, end), pageable,
        postings.size());

    return page;
  }

  public OtherMemberInfoResult getOtherMemberInfo(Long otherMemberId) {
    MemberEntity memberEntity = memberRepository.findById(otherMemberId)
        .orElseThrow(CustomMemberNotFoundException::new);

    return new OtherMemberInfoResult(memberEntity);
  }

  public PointTransferResult transferPoint(Long senderId,
      PointTransferRequest pointTransferRequest) {
    MemberEntity senderMember = memberRepository.findById(senderId)
        .orElseThrow(CustomMemberNotFoundException::new);
    MemberEntity receiverMember = memberRepository.findById(pointTransferRequest.getReceiverId())
        .orElseThrow(CustomMemberNotFoundException::new);

    if (senderMember.getPoint() < pointTransferRequest.getTransmissionPoint()) {
      throw new CustomTransferPointLackException("잔여 포인트가 부족합니다.");
    }

    int senderRemainingPoint = updateSenderPoint(senderMember,
        pointTransferRequest.getTransmissionPoint());
    int receiverRemainingPoint = updateReceiverPoint(receiverMember,
        pointTransferRequest.getTransmissionPoint());

    return new PointTransferResult(senderId, pointTransferRequest, senderRemainingPoint,
        receiverRemainingPoint);
  }

  public int updateSenderPoint(MemberEntity member, int point) {
    int remainingPoint = member.getPoint();
    member.updatePoint(remainingPoint - point);
    memberRepository.save(member);

    return member.getPoint();
  }

  public int updateReceiverPoint(MemberEntity member, int point) {
    int remainingPoint = member.getPoint();
    member.updatePoint(remainingPoint + point);
    memberRepository.save(member);

    return member.getPoint();
  }

  public void deleteMember(MemberEntity member) {
    memberRepository.delete(member);
  }

  public void decreaseCommentsLike(MemberEntity member) {
    List<MemberHasCommentLikeEntity> likes = memberHasCommentLikeRepository.findByMemberHasCommentEntityPK_MemberEntity(
        member);
    List<CommentEntity> updateComments = new ArrayList<>();
    for (MemberHasCommentLikeEntity like : likes) {
      CommentEntity comment = like.getMemberHasCommentEntityPK().getCommentEntity();
      comment.decreaseLikeCount();
      updateComments.add(comment);
      memberHasCommentLikeRepository.deleteById(like.getMemberHasCommentEntityPK());
    }
    commentRepository.saveAll(updateComments);
  }

  public void decreaseCommentsDisLike(MemberEntity member) {
    List<MemberHasCommentDislikeEntity> likes = memberHasCommentDislikeRepository.findByMemberHasCommentEntityPK_MemberEntity(
        member);
    List<CommentEntity> updateComments = new ArrayList<>();
    for (MemberHasCommentDislikeEntity like : likes) {
      CommentEntity comment = like.getMemberHasCommentEntityPK().getCommentEntity();
      comment.decreaseLikeCount();
      updateComments.add(comment);
      memberHasCommentDislikeRepository.deleteById(like.getMemberHasCommentEntityPK());
    }
    commentRepository.saveAll(updateComments);
  }

  public void deleteAccount(Long memberId) {
    // 비밀번호 인증
    // 동아리 물품, 책 미납한 기록 있으면 불가능

    MemberEntity deleted = findById(memberId);
    decreaseCommentsLike(deleted);
    decreaseCommentsDisLike(deleted);
    // 멤버 job, 친구,
    deleteMember(deleted);
  }
}
