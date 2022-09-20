package keeper.project.homepage.service.member;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.user.dto.point.request.PointLogRequestDto;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.attendance.entity.AttendanceEntity;
import keeper.project.homepage.library.entity.BookBorrowEntity;
import keeper.project.homepage.library.entity.BookEntity;
import keeper.project.homepage.member.entity.FriendEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasCommentDislikeEntity;
import keeper.project.homepage.member.entity.MemberHasCommentEntityPK;
import keeper.project.homepage.member.entity.MemberHasCommentLikeEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberHasPostingDislikeEntity;
import keeper.project.homepage.member.entity.MemberHasPostingLikeEntity;
import keeper.project.homepage.member.entity.MemberRankEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.point.entity.PointLogEntity;
import keeper.project.homepage.posting.entity.CategoryEntity;
import keeper.project.homepage.posting.entity.CommentEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.repository.member.MemberHasCommentDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasCommentLikeRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberServiceTestSetup extends ApiControllerTestHelper {

  @Autowired
  protected MemberHasCommentLikeRepository memberHasCommentLikeRepository;

  @Autowired
  protected MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;

  public MemberEntity virtualMember;
  public MemberEntity deletedMember;
  public MemberEntity writer;

  public MemberEntity follower;
  public MemberEntity followee;
  public FriendEntity follow;

  public MemberHasMemberJobEntity hasMemberJobEntity;
  public MemberRankEntity rank;
  public MemberTypeEntity type;

  public AttendanceEntity attendance;

  public CommentEntity updatedComment;
  public CommentEntity commentLikeTest;
  public MemberHasCommentLikeEntity mhcLike;
  public CommentEntity commentDislikeTest;
  public MemberHasCommentDislikeEntity mhcDislike;

  public MemberHasPostingLikeEntity mhpLike;
  public PostingEntity postLikeTest;
  public MemberHasPostingDislikeEntity mhpDislike;
  public PostingEntity postDislikeTest;

  public ThumbnailEntity thumbnailRemoveTest;

  public BookBorrowEntity borrow;

  public List<PostingEntity> virtualTestPosts = new ArrayList<>();
  public List<PostingEntity> removeTestTempPosts = new ArrayList<>();

  public PointLogEntity pointLogTest;


  public BookBorrowEntity generateBookBorrowEntity(Integer numPreventDupl, MemberEntity member) {

    final String bookTitle = "일반물리학" + numPreventDupl.toString();
    final String bookAuthor = "우웩1" + numPreventDupl.toString();
    final String bookPicture = "우우웩1" + numPreventDupl.toString() + ".png";
    final String bookInformation = "우웩우웩1";
    final Long bookQuantity = 2L;
    final Long bookBorrow = 0L;
    final Long bookEnable = bookQuantity - bookBorrow;
    final String bookRegisterDate = "20220116";

    SimpleDateFormat stringToDate = new SimpleDateFormat("yyyymmdd");
    Date registerDate = new Date();
    try {
      registerDate = stringToDate.parse(bookRegisterDate);
    } catch (Exception e) {
      e.printStackTrace();
    }
    BookEntity book = bookRepository.save(
        BookEntity.builder()
            .title(bookTitle)
            .author(bookAuthor)
            .information(bookInformation)
            .total(bookQuantity)
            .borrow(bookBorrow)
            .enable(bookEnable)
            .registerDate(registerDate)
            .build());

    return bookBorrowRepository.save(
        BookBorrowEntity.builder()
            .member(member)
            .book(book)
            .quantity(1L)
            .borrowDate(java.sql.Date.valueOf(getDate(-17)))
            .expireDate(java.sql.Date.valueOf(getDate(-3)))
            .build());
  }

  private String getDate(int date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, date);

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String transferDate = format.format(date);
    return transferDate;
  }

  // 각 test별 필요한 객체 생성
  public void generateThumbnailRemoveTestcase() {
    thumbnailRemoveTest = generateThumbnailEntity();
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    deletedMember.changeThumbnail(thumbnailRemoveTest);
    memberRepository.save(deletedMember);
  }

  public void generatePostingDislikeRemoveTestcase() {
    CategoryEntity categoryEntity = generateCategoryEntity();
    writer = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    postDislikeTest = generatePostingEntity(writer, categoryEntity, 0, 0, 0);
    mhpDislike = MemberHasPostingDislikeEntity.builder()
        .memberId(deletedMember)
        .postingId(postDislikeTest)
        .build();
    postDislikeTest.increaseDislikeCount(mhpDislike);
    postingRepository.save(postDislikeTest);
    // 객체 생성 확인
    Assertions.assertTrue(
        postingRepository.existsByMemberHasPostingDislikeEntitiesContaining(mhpDislike));
  }

  public void generatePostingLikeRemoveTestcase() {
    CategoryEntity categoryEntity = generateCategoryEntity();
    writer = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    postLikeTest = generatePostingEntity(writer, categoryEntity, 0, 0, 0);
    mhpLike = MemberHasPostingLikeEntity.builder()
        .memberId(deletedMember)
        .postingId(postLikeTest)
        .build();
    postLikeTest.increaseLikeCount(mhpLike);
    postingRepository.save(postLikeTest);
    // 객체가 잘 생성되었는지 확인
    Assertions.assertTrue(postingRepository.existsByMemberHasPostingLikeEntitiesContaining(
        mhpLike));
  }

  public void generateTempPostingRemovedTestcase() {
    CategoryEntity categoryEntity = generateCategoryEntity();
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    for (int i = 0; i < 3; i++) {
      removeTestTempPosts.add(
          generatePostingEntity(deletedMember, categoryEntity, 0, 0, 1));
    }
  }

  public void generatePostingChangeToVirtualMemberTestcase() {
    CategoryEntity categoryEntity = generateCategoryEntity();
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    for (int i = 0; i < 3; i++) {
      virtualTestPosts.add(
          generatePostingEntity(deletedMember, categoryEntity, 0, 0, 0));
    }
  }

  public void generateCommentChangeToVirtualMemberTestcase() {
    CategoryEntity categoryEntity = generateCategoryEntity();
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    PostingEntity post = generatePostingEntity(deletedMember, categoryEntity, 0, 0, 0);
    updatedComment = generateCommentEntity(post, deletedMember, deletedMember.getId());
  }


  public void generateCommentDislikeRemoveTestcase() {
    CategoryEntity categoryEntity = generateCategoryEntity();
    writer = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    PostingEntity post = generatePostingEntity(writer, categoryEntity, 0, 0, 0);
    commentDislikeTest = generateCommentEntity(post, writer, writer.getId());
    commentService.updateDislikeCount(deletedMember.getId(), commentDislikeTest.getId());
    mhcDislike = memberHasCommentDislikeRepository.findById(
        new MemberHasCommentEntityPK(deletedMember, commentDislikeTest)).orElse(null);
    // 테스트 전 객체 생성 확인
    Assertions.assertNotNull(mhcDislike, "존재하지 않는 MemberHasCommentDislike 입니다.");
  }

  public void generateCommentLikeRemoveTestcase() {
    CategoryEntity categoryEntity = generateCategoryEntity();
    writer = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    PostingEntity post = generatePostingEntity(writer, categoryEntity, 0, 0, 0);
    commentLikeTest = generateCommentEntity(post, writer, writer.getId());
    commentService.updateLikeCount(deletedMember.getId(), commentLikeTest.getId());
    mhcLike = memberHasCommentLikeRepository.findById(
        new MemberHasCommentEntityPK(deletedMember, commentLikeTest)).orElse(null);
    // 테스트 전 객체 생성 확인
    Assertions.assertNotNull(mhcLike, "존재하지 않는 MemberHasCommentLikeEntity 입니다.");
  }

  public void generateRankAndTypeRemoveTestcase() {
    // extra member
    generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.우수회원);
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.우수회원);
    rank = deletedMember.getMemberRank();
    type = deletedMember.getMemberType();
  }

  public void generateJobCascadeRemoveTestcase() {
    // extra member
    generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.우수회원);
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    hasMemberJobEntity = deletedMember.getMemberJobs().get(0);
  }

  public void generateFriendCascadeRemoveTestcase() {
    follower = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    followee = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    follow = friendRepository.save(
        FriendEntity.builder()
            .follower(follower)
            .followee(followee)
            .registerDate(LocalDate.now())
            .build());
    follower.getFollowee().add(follow);
    followee.getFollower().add(follow);
  }

  public void generateAttendanceRemoveTestcase() {
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    Random random = new Random();
    System.out.println(LocalDateTime.now().toLocalDate());
    System.out.println(LocalDate.now());
    System.out.println("AAAAAAAAAAAAAAA");
    attendance = attendanceRepository.save(
        AttendanceEntity.builder()
            .time(LocalDateTime.now())
            .date(LocalDateTime.now().toLocalDate())
            .point(10)
            .rankPoint(30)
            .continuousPoint(0)
            .randomPoint((int) (Math.random() * 900 + 100))
            .ipAddress("111.111.111.111")
            .greetings("hi")
            .continuousDay(1)
            .rank(3)
            .rankPoint(30)
            .randomPoint((int) (Math.random() * 900 + 100))
            .member(deletedMember)
            .build());

    // 다른 출석 기록에 영향을 안 끼치는 지 확인용
    MemberEntity otherMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
        MemberRankName.일반회원);
    attendanceRepository.save(
        AttendanceEntity.builder()
            .point(10)
            .continuousPoint(0)
            .continuousDay(0)
            .greetings("hi")
            .ipAddress("111.111.111.111")
            .time(LocalDateTime.now())
            .date(LocalDate.now())
            .member(otherMember)
            .rank(3)
            .rankPoint(30)
            .randomPoint((int) (Math.random() * 900 + 100)).build());
  }

  public void generateCheckRemainBorrowInfoTestcase() {
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    borrow = generateBookBorrowEntity(1, deletedMember);
  }

  public void generateCheckCorrectPasswordTestcase() {
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
  }

  public void generatePointLogRemoveTestcase() {
    deletedMember = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    PointLogRequestDto pointLog = new PointLogRequestDto();
    pointLog.setTime(LocalDateTime.now());
    pointLog.setPoint(10);
    pointLog.setDetail("테스트 용 포인트 저장하기");

    deletedMember.updatePoint(pointLog.getPoint());
    memberRepository.save(deletedMember);
    pointLogTest = pointLogRepository.save(pointLog.toEntity(deletedMember, 0));

    // 다른 회원 로그는 삭제되지 않는지 테스트 용
    MemberEntity another = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
        MemberRankName.일반회원);

    another.updatePoint(pointLog.getPoint());
    memberRepository.save(another);
    pointLogRepository.save(pointLog.toEntity(another, 0));
  }
}
