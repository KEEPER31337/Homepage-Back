package keeper.project.homepage.repository.posting;

import java.time.LocalDateTime;
import java.util.Optional;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.posting.entity.CategoryEntity;
import keeper.project.homepage.posting.entity.CommentEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  @Autowired
  protected MemberJobRepository memberJobRepository;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  private LocalDateTime registerTime = LocalDateTime.now();
  private LocalDateTime updateTime = LocalDateTime.now();
  private String ipAddress = "127.0.0.1";
  private Integer likeCount = 0;
  private Integer dislikeCount = 0;

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";

  private CommentEntity commentEntity;
  private MemberEntity memberEntity;

  @BeforeEach
  public void setup() {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .generation(0F)
        .build();
    memberEntity.addMemberJob(memberJobEntity);
    memberRepository.save(memberEntity);

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
        .registerTime(LocalDateTime.now())
        .updateTime(LocalDateTime.now())
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
        .member(memberEntity)
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
        .member(memberEntity)
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
        .member(memberEntity)
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
    original.changeContent("MMMoDDDiFFFy");
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
