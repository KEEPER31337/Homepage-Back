package keeper.project.homepage.repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.MemberHasCommentLikeEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.entity.identifier.MemberHasCommentLikeId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberHasCommentLikeRepositoryTest {

  private Logger LOGGER = LogManager.getLogger();

  @Autowired
  private MemberHasCommentLikeRepository memberHasCommentLikeRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private PostingRepository postingRepository;

  @Autowired
  private MemberRepository memberRepository;

  private CommentEntity commentEntity;
  private CommentEntity parentComment;
  private MemberEntity memberEntity;
  private MemberHasCommentLikeEntity memberHasCommentLikeEntity;

  private LocalDate registerTime = LocalDate.now();
  private LocalDate updateTime = LocalDate.now();
  private String ipAddress = "127.0.0.1";
  private Integer likeCount = 0;
  private Integer dislikeCount = 0;

  @BeforeEach
  public void setup() {
    memberEntity = memberRepository.save(MemberEntity.builder()
        .loginId("로그인")
        .password("비밀번호")
        .realName("이름")
        .nickName("닉네임")
        .emailAddress("이메일")
        .studentId("학번")
        .roles(Collections.singletonList("ROLE_USER")).build());
    CategoryEntity categoryEntity = categoryRepository.save(
        CategoryEntity.builder().name("test category").build());

    PostingEntity posting = postingRepository.save(PostingEntity.builder()
        .title("posting 제목")
        .content("posting 내용")
        .categoryId(categoryEntity)
        .ipAddress("192.111.222.333")
        .allowComment(0)
        .isNotice(0)
        .isSecret(1)
        .likeCount(10)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .memberId(memberEntity)
        .password("asdsdf")
        .build());

    parentComment = commentRepository.save(CommentEntity.builder()
        .content("부모 댓글 내용")
        .registerTime(registerTime)
        .updateTime(updateTime)
        .ipAddress(ipAddress)
        .likeCount(likeCount)
        .dislikeCount(dislikeCount)
        .parentId(0L)
        .memberId(memberEntity)
        .postingId(posting)
        .build());

    commentEntity = commentRepository.save(CommentEntity.builder()
        .content("댓글 내용")
        .registerTime(registerTime)
        .updateTime(updateTime)
        .ipAddress(ipAddress)
        .likeCount(likeCount)
        .dislikeCount(dislikeCount)
        .parentId(parentComment.getId())
        .memberId(memberEntity)
        .postingId(posting)
        .build());

    memberHasCommentLikeEntity = memberHasCommentLikeRepository.save(
//        MemberHasCommentLikeEntity.builder().commentEntity(commentEntity).memberEntity(memberEntity)
//            .build());
//    LOGGER.info(String.format("create한 member_xx_like: (comment : %ll, member : %ll)",
//        newMHCL.getCommentEntity().getId(), newMHCL.getMemberEntity().getId()));
        MemberHasCommentLikeEntity.builder().memberHasCommentLikeId(
            new MemberHasCommentLikeId(memberEntity, commentEntity)).build());
  }

  @Test
  @DisplayName("\"멤버-댓글 좋아요\" 생성")
  public void createTest() {
    LOGGER.info("comment id : " + commentEntity.getId());
    LOGGER.info("member id : " + memberEntity.getId());
    MemberHasCommentLikeEntity newMHCL = memberHasCommentLikeRepository.save(
        MemberHasCommentLikeEntity.builder().memberHasCommentLikeId(
            new MemberHasCommentLikeId(memberEntity, parentComment)).build());
    LOGGER.info(String.format("create한 member_xx_like: (comment : %d, member : %d)",
        newMHCL.getMemberHasCommentLikeId().getCommentEntity().getId(),
        newMHCL.getMemberHasCommentLikeId().getMemberEntity().getId()));

  }

  @Test
  @DisplayName("댓글 id로 \"멤버-댓글 좋아요\" 조회")
  public void findByCommentIdTest() {
    List<MemberHasCommentLikeEntity> findMHCLs = memberHasCommentLikeRepository.findByMemberHasCommentLikeId_CommentEntity(
        commentEntity);
    findMHCLs.forEach(
        mhcl -> LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
            mhcl.getMemberHasCommentLikeId().getCommentEntity().getId(),
            mhcl.getMemberHasCommentLikeId().getMemberEntity().getId())));
    Assertions.assertTrue(!findMHCLs.isEmpty());
  }

  @Test
  @DisplayName("멤버 id로 \"멤버-댓글 좋아요\" 조회")
  public void findByMemberIdTest() {
    List<MemberHasCommentLikeEntity> findMHCLs = memberHasCommentLikeRepository.findByMemberHasCommentLikeId_MemberEntity(
        memberEntity);
    findMHCLs.forEach(
        mhcl -> LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
            mhcl.getMemberHasCommentLikeId().getCommentEntity().getId(),
            mhcl.getMemberHasCommentLikeId().getMemberEntity().getId())));
    Assertions.assertTrue(!findMHCLs.isEmpty());
  }

  @Test
  @DisplayName("\"멤버-댓글 좋아요\" 전체 리스트 조회")
  public void findAllTest() {
    List<MemberHasCommentLikeEntity> findMHCLs = memberHasCommentLikeRepository.findAll();
    findMHCLs.forEach(
        mhcl -> LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
            mhcl.getMemberHasCommentLikeId().getCommentEntity().getId(),
            mhcl.getMemberHasCommentLikeId().getMemberEntity().getId())));
    Assertions.assertTrue(!findMHCLs.isEmpty());
  }

  @Test
  @DisplayName("멤버 id와 댓글 id로 \"멤버-댓글 좋아요\" 조회")
  public void findByMemberHasCommentLikeIdTest() {
    Optional<MemberHasCommentLikeEntity> mhcl = memberHasCommentLikeRepository.findById(
        new MemberHasCommentLikeId(memberEntity, commentEntity));
    Assertions.assertTrue(mhcl.isPresent());
    LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
        mhcl.get().getMemberHasCommentLikeId().getCommentEntity().getId(),
        mhcl.get().getMemberHasCommentLikeId().getMemberEntity().getId()));
  }

  @Test
  @DisplayName("멤버 id와 댓글 id로 \"멤버-댓글 좋아요\" 삭제 - 해당 멤버가 댓글의 좋아요를 취소한 경우")
  public void deleteByMemberHasCommentLikeIdTest() {
    List<MemberHasCommentLikeEntity> findMHCLs = memberHasCommentLikeRepository.findAll();
    findMHCLs.forEach(
        mhcl -> LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
            mhcl.getMemberHasCommentLikeId().getCommentEntity().getId(),
            mhcl.getMemberHasCommentLikeId().getMemberEntity().getId())));

    LOGGER.info(String.format("delete id : (member_id = %d, comment_id = %d)", memberEntity.getId(),
        commentEntity.getId()));
    memberHasCommentLikeRepository.deleteById(
        new MemberHasCommentLikeId(memberEntity, commentEntity));

    findMHCLs = memberHasCommentLikeRepository.findAll();
    if (findMHCLs.isEmpty()) {
      LOGGER.info("MemberHasCommentLike table is empty");
    } else {
      findMHCLs.forEach(
          mhcl -> LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
              mhcl.getMemberHasCommentLikeId().getCommentEntity().getId(),
              mhcl.getMemberHasCommentLikeId().getMemberEntity().getId())));
    }
  }

  @Test
  @DisplayName("댓글 id로 \"멤버-댓글 좋아요\" 삭제 - 해당 댓글이 삭제된 경우")
  public void deleteByCommentIdTest() {
    List<MemberHasCommentLikeEntity> findMHCLs = memberHasCommentLikeRepository.findAll();
    findMHCLs.forEach(
        mhcl -> LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
            mhcl.getMemberHasCommentLikeId().getCommentEntity().getId(),
            mhcl.getMemberHasCommentLikeId().getMemberEntity().getId())));

    LOGGER.info(String.format("delete comment_id = %d", commentEntity.getId()));
    memberHasCommentLikeRepository.deleteByMemberHasCommentLikeId_CommentEntity(commentEntity);

    findMHCLs = memberHasCommentLikeRepository.findAll();
    if (findMHCLs.isEmpty()) {
      LOGGER.info("MemberHasCommentLike table is empty");
    } else {
      findMHCLs.forEach(
          mhcl -> LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
              mhcl.getMemberHasCommentLikeId().getCommentEntity().getId(),
              mhcl.getMemberHasCommentLikeId().getMemberEntity().getId())));
    }
  }

  @Test
  @DisplayName("멤버 id로 \"멤버-댓글 좋아요\" 삭제 - 해당 멤버 정보가 삭제된 경우")
  public void deleteByMemberIdTest() {
    List<MemberHasCommentLikeEntity> findMHCLs = memberHasCommentLikeRepository.findAll();
    findMHCLs.forEach(
        mhcl -> LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
            mhcl.getMemberHasCommentLikeId().getCommentEntity().getId(),
            mhcl.getMemberHasCommentLikeId().getMemberEntity().getId())));

    LOGGER.info(String.format("delete member_id = %d", memberEntity.getId()));
    memberHasCommentLikeRepository.deleteByMemberHasCommentLikeId_MemberEntity(memberEntity);

    findMHCLs = memberHasCommentLikeRepository.findAll();
    if (findMHCLs.isEmpty()) {
      LOGGER.info("MemberHasCommentLike table is empty");
    } else {
      findMHCLs.forEach(
          mhcl -> LOGGER.info(String.format("found member_xx_like: (comment : %d, member : %d)",
              mhcl.getMemberHasCommentLikeId().getCommentEntity().getId(),
              mhcl.getMemberHasCommentLikeId().getMemberEntity().getId())));
    }
  }
}