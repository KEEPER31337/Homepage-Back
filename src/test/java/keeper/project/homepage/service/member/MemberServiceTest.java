package keeper.project.homepage.service.member;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasCommentEntityPK;
import keeper.project.homepage.entity.member.MemberHasCommentLikeEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.attendance.AttendanceRepository;
import keeper.project.homepage.repository.member.FriendRepository;
import keeper.project.homepage.repository.member.MemberHasCommentLikeRepository;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRankRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.repository.posting.CommentRepository;
import keeper.project.homepage.repository.posting.PostingRepository;
import keeper.project.homepage.service.posting.CommentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private MemberJobRepository memberJobRepository;

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberHasMemberJobRepository memberHasMemberJobRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private FriendRepository friendRepository;

  @Autowired
  private AttendanceRepository attendanceRepository;

  @Autowired
  private MemberRankRepository memberRankRepository;

  @Autowired
  private MemberTypeRepository memberTypeRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private PostingRepository postingRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CommentService commentService;

  @Autowired
  private MemberHasCommentLikeRepository memberHasCommentLikeRepository;

  private MemberEntity jobTest;
  private MemberHasMemberJobEntity hasMemberJobEntity;

  private MemberEntity follower;
  private MemberEntity followee;
  private FriendEntity follow;

  private MemberEntity rankAndTypeTest;
  private MemberRankEntity rank;
  private MemberTypeEntity type;

  private MemberEntity attendanceTest;
  private AttendanceEntity attendance;

  private MemberEntity commentLikeTestMember;
  private CommentEntity commentLike;
  private MemberHasCommentLikeEntity memberHasCommentLike;

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";

  @BeforeEach
  public void setup() throws Exception {

  }

  @Test
  @DisplayName("Member id로 조회")
  public void findByIdTest() {
    MemberEntity findTest = generateMemberEntity(1);

    Long findId = findTest.getId();
    Optional<MemberEntity> findMember = memberRepository.findById(findId);
    Assertions.assertTrue(findMember.isPresent());
  }

  @Test
  @DisplayName("Member와 MemberHasMemberJob의 연관 관계 확인")
  public void jobRelationTest() {
    generateJobCascadeRemoveTestcase();

    // 자식 필드가 제대로 설정이 됐는지 확인
    Assertions.assertTrue(
        memberHasMemberJobRepository.findById(hasMemberJobEntity.getId()).isPresent());
    Assertions.assertTrue(
        hasMemberJobEntity.getMemberEntity().getId().equals(jobTest.getId()));
    Assertions.assertTrue(jobTest.getMemberJobs().contains(hasMemberJobEntity));
  }

  @Test
  @DisplayName("회원 삭제 시, MemberHasMemberJob 하위 필드 자동 삭제 테스트")
  public void jobCascadeRemoveTest() {
    generateJobCascadeRemoveTestcase();

    // 삭제 전 연관관계 확인
    Assertions.assertTrue(jobTest.getMemberJobs().contains(hasMemberJobEntity));

    memberService.deleteMember(jobTest);

    // CascadeType.REMOVE - 삭제 후 하위 레코드까지 삭제 됐는지 확인
    Assertions.assertTrue(memberRepository.findById(jobTest.getId()).isEmpty());
    Assertions.assertTrue(
        memberHasMemberJobRepository.findById(hasMemberJobEntity.getId()).isEmpty());
  }

  @Test
  @DisplayName("회원 삭제 시, FriendEntity 하위 필드 자동 삭제 테스트")
  public void friendCascadeRemoveTest() {
    generateFriendCascadeRemoveTestcase();

    // 삭제 전 연관관계 확인
    Assertions.assertTrue(followee.getFollower().contains(follow));
    Assertions.assertTrue(follower.getFollowee().contains(follow));

    Integer befFolloweeCnt = follower.getFollowee().size();
    memberService.deleteMember(followee);

    // CascadeType.REMOVE - 삭제 후 하위 레코드까지 삭제 됐는지 확인
    Assertions.assertTrue(memberRepository.findById(followee.getId()).isEmpty());
    Assertions.assertTrue(friendRepository.findById(follow.getId()).isEmpty());

    // follower의 list에서도 FriendEntity 삭제가 반영되었는지 확인
    Assertions.assertFalse(follower.getFollower().contains(follow));
    Assertions.assertTrue(follower.getFollower().size() == befFolloweeCnt - 1);
  }

  @Test
  @DisplayName("회원 삭제 시, MemberRankEntity & MemberTypeEntity에서 삭제 되었는지 확인")
  public void rankAndTypeRemoveTest() {
    generateRankAndTypeRemoveTestcase();

    memberService.deleteMember(rankAndTypeTest);

    Assertions.assertFalse(rank.getMembers().contains(rankAndTypeTest));
    Assertions.assertFalse(type.getMembers().contains(rankAndTypeTest));
  }

  @Test
  @DisplayName("회원 삭제 시, 댓글 좋아요 삭제 확인")
  public void commentLikeRemoveTest() {
    generateCommentLikeRemoveTestcase();
    int likeCount = commentLike.getLikeCount();

    memberService.decreaseCommentsLike(commentLikeTestMember);

    Assertions.assertTrue(
        memberHasCommentLikeRepository.findById(memberHasCommentLike.getMemberHasCommentEntityPK())
            .isEmpty());
    Assertions.assertTrue(
        commentLike.getLikeCount().equals(likeCount - 1)); // 이건 자동으로 안 됨. comment 돌면서 수동으로 감해줘야 함.
  }

  public void generateCommentLikeRemoveTestcase() {
    commentLikeTestMember = generateMemberEntity(1);
    MemberEntity writer = generateMemberEntity(2);
    PostingEntity post = generatePostingEntity(1, writer);
    commentLike = generateCommentEntity(1, writer, post);
    commentService.updateLikeCount(commentLikeTestMember.getId(), commentLike.getId());
    memberHasCommentLike = memberHasCommentLikeRepository.findById(
        new MemberHasCommentEntityPK(commentLikeTestMember, commentLike)).orElse(null);
    // 테스트 전 객체 생성 확인
    Assertions.assertNotNull(memberHasCommentLike, "존재하지 않는 MemberHasCommentLikeEntity 입니다.");
  }

  public PostingEntity generatePostingEntity(Integer numPreventDupl, MemberEntity memberEntity) {
    CategoryEntity categoryEntity = categoryRepository.save(
        CategoryEntity.builder()
            .name("test category" + numPreventDupl)
            .build());
    return postingRepository.save(PostingEntity.builder()
        .title("posting 제목" + numPreventDupl)
        .content("posting 내용" + numPreventDupl)
        .categoryId(categoryEntity)
        .ipAddress("192.111.222.333")
        .allowComment(0)
        .isNotice(0)
        .isSecret(1)
        .isTemp(0)
        .likeCount(10)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .memberId(memberEntity)
        .password("pw" + numPreventDupl)
        .build());
  }

  public CommentEntity generateCommentEntity(Integer numPreventDupl, MemberEntity memberEntity,
      PostingEntity postingEntity) {
    return commentRepository.save(CommentEntity.builder()
        .content("댓글 내용" + numPreventDupl)
        .registerTime(LocalDate.now())
        .updateTime(LocalDate.now())
        .ipAddress("111.111.111.111")
        .likeCount(0)
        .dislikeCount(0)
        .parentId(0L)
        .memberId(memberEntity)
        .postingId(postingEntity)
        .build());
  }

  public void generateRankAndTypeRemoveTestcase() {
    rankAndTypeTest = generateMemberEntity(1);
    rank = memberRankRepository.findByName("우수회원").get();
    type = memberTypeRepository.findByName("정회원").get();
    rankAndTypeTest.changeMemberRank(rank);
    rankAndTypeTest.changeMemberType(type);
  }
  // TODO : attendance 양방향 연결 후 test 추가
//  @Test
//  public void attendanceCascadeRemoveTest() {
//    generateAttendanceCascadeRemoveTestcase();
//
//    memberService.deleteMember(attendanceTest.getId());
//
//    Assertions.assertTrue(memberRepository.findById(attendanceTest.getId()).isEmpty());
//    Assertions.assertTrue(attendanceRepository.findById(attendance.getId()).isEmpty());
//  }

  public MemberEntity generateMemberEntity(Integer preventDuplNum) {
    return memberRepository.save(MemberEntity.builder()
        .loginId(loginId + preventDuplNum.toString())
        .password(passwordEncoder.encode(password + preventDuplNum.toString()))
        .realName(realName + preventDuplNum.toString())
        .nickName(nickName + preventDuplNum.toString())
        .emailAddress(emailAddress + preventDuplNum.toString())
        .studentId(studentId + preventDuplNum.toString())
        .build());
  }

  public void generateJobCascadeRemoveTestcase() {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    jobTest = generateMemberEntity(1);
    hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberEntity(jobTest)
        .memberJobEntity(memberJobEntity)
        .build();
    memberHasMemberJobRepository.save(hasMemberJobEntity);
    jobTest.getMemberJobs().add(hasMemberJobEntity);

  }

  public void generateFriendCascadeRemoveTestcase() {
    follower = generateMemberEntity(2);
    followee = generateMemberEntity(3);
    follow = friendRepository.save(
        FriendEntity.builder()
            .follower(follower)
            .followee(followee)
            .registerDate(LocalDate.now())
            .build());
    follower.getFollowee().add(follow);
    followee.getFollower().add(follow);
  }

  private void generateAttendanceCascadeRemoveTestcase() {
    attendanceTest = generateMemberEntity(3);
    Random random = new Random();
    attendance = attendanceRepository.save(
        AttendanceEntity.builder()
            .point(10)
            .continousDay(0)
            .greetings("hi")
            .ipAddress("111.111.111.111")
            .time(Timestamp.valueOf(LocalDateTime.now()))
            .memberId(attendanceTest)
            .rank(3)
            .randomPoint(random.nextInt(100, 1001)).build());
  }

}
