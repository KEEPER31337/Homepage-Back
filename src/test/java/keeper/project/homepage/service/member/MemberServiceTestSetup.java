package keeper.project.homepage.service.member;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.common.FileConversion;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import keeper.project.homepage.entity.member.FriendEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasCommentDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasCommentEntityPK;
import keeper.project.homepage.entity.member.MemberHasCommentLikeEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasPostingLikeEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.FileRepository;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.repository.attendance.AttendanceRepository;
import keeper.project.homepage.repository.member.FriendRepository;
import keeper.project.homepage.repository.member.MemberHasCommentDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasCommentLikeRepository;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberHasPostingDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasPostingLikeRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRankRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.repository.posting.CommentRepository;
import keeper.project.homepage.repository.posting.PostingRepository;
import keeper.project.homepage.service.posting.CommentService;
import keeper.project.homepage.service.posting.PostingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberServiceTestSetup {

  @Autowired
  public MemberRepository memberRepository;

  @Autowired
  public MemberJobRepository memberJobRepository;

  @Autowired
  public MemberService memberService;

  @Autowired
  public MemberHasMemberJobRepository memberHasMemberJobRepository;

  @Autowired
  public PasswordEncoder passwordEncoder;

  @Autowired
  public FriendRepository friendRepository;

  @Autowired
  public AttendanceRepository attendanceRepository;

  @Autowired
  public MemberRankRepository memberRankRepository;

  @Autowired
  public MemberTypeRepository memberTypeRepository;

  @Autowired
  public CommentRepository commentRepository;

  @Autowired
  public PostingRepository postingRepository;

  @Autowired
  public CategoryRepository categoryRepository;

  @Autowired
  public CommentService commentService;

  @Autowired
  public PostingService postingService;

  @Autowired
  public MemberHasCommentLikeRepository memberHasCommentLikeRepository;

  @Autowired
  public MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;

  @Autowired
  public MemberHasPostingLikeRepository memberHasPostingLikeRepository;

  @Autowired
  public MemberHasPostingDislikeRepository memberHasPostingDislikeRepository;

  @Autowired
  public FileRepository fileRepository;

  @Autowired
  public ThumbnailRepository thumbnailRepository;

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

  public List<PostingEntity> virtualTestPosts = new ArrayList<>();
  public List<PostingEntity> removeTestTempPosts = new ArrayList<>();

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";


  public MemberEntity generateMemberEntity(Integer numPreventDupl) {
    return memberRepository.save(MemberEntity.builder()
        .loginId(loginId + numPreventDupl.toString())
        .password(passwordEncoder.encode(password + numPreventDupl.toString()))
        .realName(realName + numPreventDupl.toString())
        .nickName(nickName + numPreventDupl.toString())
        .emailAddress(emailAddress + numPreventDupl.toString())
        .studentId(studentId + numPreventDupl.toString())
        .build());
  }

  public PostingEntity generatePostingEntity(Integer numPreventDupl, MemberEntity memberEntity,
      Integer checkTemp) {
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
        .isTemp(checkTemp)
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

  public ThumbnailEntity generateThumbnailEntity(Integer numPreventDupl) {
    final String userDirectory = System.getProperty("user.dir");
    final String keeperFilesDirectoryPath = "keeper_files";
    final String thumbnailDirectoryPath = "keeper_files" + File.separator + "thumbnail";
    final String imageName = "mem_image" + numPreventDupl.toString() + ".jpg";
    final String thumbnailName = "thumb_mem_image" + numPreventDupl.toString() + ".jpg";

    File keeperFilesDir = new File(userDirectory + File.separator + keeperFilesDirectoryPath);
    File thumbnailDir = new File(userDirectory + File.separator + thumbnailDirectoryPath);
    if (!keeperFilesDir.exists()) {
      keeperFilesDir.mkdir();
    }
    if (!thumbnailDir.exists()) {
      thumbnailDir.mkdir();
    }

    long imageSize = createImageForTest(
        userDirectory + File.separator + keeperFilesDirectoryPath + File.separator + imageName);
    createImageForTest(
        userDirectory + File.separator + thumbnailDirectoryPath + File.separator + thumbnailName);

    FileEntity file = fileRepository.save(
        FileEntity.builder()
            .fileName(imageName)
            .filePath(keeperFilesDirectoryPath + File.separator + imageName)
            .fileSize(imageSize)
            .ipAddress("111.111.111.111")
            .build());
    return thumbnailRepository.save(
        ThumbnailEntity.builder()
            .path(thumbnailDirectoryPath + File.separator + thumbnailName)
            .file(file)
            .build());
  }

  private long createImageForTest(String filePath) {
    FileConversion fileConversion = new FileConversion();
    fileConversion.makeSampleJPEGImage(filePath);
    File createdFile = new File(filePath);
    return createdFile.length();
  }

  // 각 test별 필요한 객체 생성
  public void generateThumbnailRemoveTestcase() {
    thumbnailRemoveTest = generateThumbnailEntity(1);
    deletedMember = generateMemberEntity(1);
    deletedMember.changeThumbnail(thumbnailRemoveTest);
    memberRepository.save(deletedMember);
  }

  public void generatePostingDislikeRemoveTestcase() {
    writer = generateMemberEntity(1);
    deletedMember = generateMemberEntity(2);
    postDislikeTest = generatePostingEntity(1, writer, PostingService.isNotTempPosting);
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
    writer = generateMemberEntity(1);
    deletedMember = generateMemberEntity(2);
    postLikeTest = generatePostingEntity(1, writer, PostingService.isNotTempPosting);
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
    deletedMember = generateMemberEntity(1);
    for (int i = 0; i < 3; i++) {
      removeTestTempPosts.add(
          generatePostingEntity(i, deletedMember, PostingService.isTempPosting));
    }
  }

  public void generatePostingChangeToVirtualMemberTestcase() {
    deletedMember = generateMemberEntity(1);
    for (int i = 0; i < 3; i++) {
      virtualTestPosts.add(
          generatePostingEntity(i, deletedMember, PostingService.isNotTempPosting));
    }
  }

  public void generateCommentChangeToVirtualMemberTestcase() {
    deletedMember = generateMemberEntity(1);
    PostingEntity post = generatePostingEntity(1, deletedMember, PostingService.isNotTempPosting);
    updatedComment = generateCommentEntity(1, deletedMember, post);
  }


  public void generateCommentDislikeRemoveTestcase() {
    writer = generateMemberEntity(1);
    deletedMember = generateMemberEntity(2);
    PostingEntity post = generatePostingEntity(1, writer, PostingService.isNotTempPosting);
    commentDislikeTest = generateCommentEntity(1, writer, post);
    commentService.updateDislikeCount(deletedMember.getId(), commentDislikeTest.getId());
    mhcDislike = memberHasCommentDislikeRepository.findById(
        new MemberHasCommentEntityPK(deletedMember, commentDislikeTest)).orElse(null);
    // 테스트 전 객체 생성 확인
    Assertions.assertNotNull(mhcDislike, "존재하지 않는 MemberHasCommentDislike 입니다.");
  }

  public void generateCommentLikeRemoveTestcase() {
    writer = generateMemberEntity(1);
    deletedMember = generateMemberEntity(2);
    PostingEntity post = generatePostingEntity(1, writer, PostingService.isNotTempPosting);
    commentLikeTest = generateCommentEntity(1, writer, post);
    commentService.updateLikeCount(deletedMember.getId(), commentLikeTest.getId());
    mhcLike = memberHasCommentLikeRepository.findById(
        new MemberHasCommentEntityPK(deletedMember, commentLikeTest)).orElse(null);
    // 테스트 전 객체 생성 확인
    Assertions.assertNotNull(mhcLike, "존재하지 않는 MemberHasCommentLikeEntity 입니다.");
  }

  public void generateRankAndTypeRemoveTestcase() {
    deletedMember = generateMemberEntity(1);
    rank = memberRankRepository.findByName("우수회원").get();
    type = memberTypeRepository.findByName("정회원").get();
    deletedMember.changeMemberRank(rank);
    deletedMember.changeMemberType(type);
  }

  public void generateJobCascadeRemoveTestcase() {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    deletedMember = generateMemberEntity(1);
    hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberEntity(deletedMember)
        .memberJobEntity(memberJobEntity)
        .build();
    memberHasMemberJobRepository.save(hasMemberJobEntity);
    deletedMember.getMemberJobs().add(hasMemberJobEntity);

  }

  public void generateFriendCascadeRemoveTestcase() {
    follower = generateMemberEntity(1);
    followee = generateMemberEntity(2);
    follow = friendRepository.save(
        FriendEntity.builder()
            .follower(follower)
            .followee(followee)
            .registerDate(LocalDate.now())
            .build());
    follower.getFollowee().add(follow);
    followee.getFollower().add(follow);
  }

  // TODO : attendance 양방향 연결 후 test 추가
//  public void generateAttendanceCascadeRemoveTestcase() {
//    attendanceTest = generateMemberEntity(1);
//    Random random = new Random();
//    attendance = attendanceRepository.save(
//        AttendanceEntity.builder()
//            .point(10)
//            .continousDay(0)
//            .greetings("hi")
//            .ipAddress("111.111.111.111")
//            .time(Timestamp.valueOf(LocalDateTime.now()))
//            .memberId(attendanceTest)
//            .rank(3)
//            .randomPoint(random.nextInt(100, 1001)).build());
//  }


}
