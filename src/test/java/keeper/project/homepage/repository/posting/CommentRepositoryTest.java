package keeper.project.homepage.repository.posting;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.repository.posting.CommentRepository;
import keeper.project.homepage.repository.posting.PostingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

  private Logger LOGGER = LogManager.getLogger();
  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private PostingRepository postingRepository;

  @Autowired
  private MemberRepository memberRepository;

  private LocalDate registerTime = LocalDate.now();
  private LocalDate updateTime = LocalDate.now();
  private String ipAddress = "127.0.0.1";
  private Integer likeCount = 0;
  private Integer dislikeCount = 0;

  private CommentEntity commentEntity;
  private MemberEntity memberEntity;

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
        .isTemp(0)
        .likeCount(10)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .memberId(memberEntity)
        .password("asdsdf")
        .build());

    CommentEntity parentComment = commentRepository.save(CommentEntity.builder()
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

  }

  @Test
  public void createTest() {
    CommentEntity newComment = commentRepository.save(CommentEntity.builder()
        .content("댓글 저장")
        .registerTime(registerTime)
        .updateTime(updateTime)
        .ipAddress(ipAddress)
        .likeCount(likeCount)
        .dislikeCount(dislikeCount)
        .parentId(commentEntity.getId())
        .memberId(memberEntity)
        .postingId(commentEntity.getPostingId())
        .build());
    LOGGER.info("create한 comment의 id : " + newComment.getId().toString());
    commentRepository.findAll().forEach(comment -> LOGGER.info(comment.getId()));
  }

  @Test
  public void findTest() {
    Optional<CommentEntity> commentSelect = commentRepository.findById(commentEntity.getId());
    Assertions.assertTrue(commentSelect.isPresent());
  }

  @Test
  public void viewAllTest() {
    commentRepository.findAll().forEach(comment -> LOGGER.info(comment.getId()));
  }

  @Test
  public void updateTest() {
    Long updateId = commentEntity.getId();
    Optional<CommentEntity> commentSelect = commentRepository.findById(updateId);
    Assertions.assertTrue(commentSelect.isPresent(), "Entity not found");

    LOGGER.info("수정 전 content : " + commentSelect.get().getContent());
    CommentEntity original = commentSelect.get();
    CommentEntity temp = CommentEntity.builder()
        .content("MMMoDDDiFFFy").likeCount(original.getLikeCount())
        .dislikeCount(original.getDislikeCount()).updateTime(LocalDate.now())
        .ipAddress(original.getIpAddress()).build();
    original.changeProperties(temp);
    commentRepository.save(original);
    LOGGER.info("수정 후 content : " + commentRepository.findById(updateId).get().getContent());

    Assertions.assertEquals(commentRepository.findById(updateId).get().getContent(),
        original.getContent());
    Assertions.assertEquals(original.getContent(), "MMMoDDDiFFFy");
  }

  @Test
  public void deleteTest() {
    Long testId = commentEntity.getId();
    commentRepository.deleteById(testId);
    Assertions.assertTrue(!commentRepository.findById(testId).isPresent());
  }

}
