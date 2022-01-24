package keeper.project.homepage.repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.PostingEntity;
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

  private String content = "댓글 내용";
  private LocalDate registerTime = LocalDate.now();
  private LocalDate updateTime = LocalDate.now();
  private String ipAddress = "127.0.0.1";
  private Integer likeCount = 0;
  private Integer dislikeCount = 0;
//  private Integer memberId = 10;

  private CommentEntity commentEntity;

  @BeforeEach
  public void setup() {
    CategoryEntity categoryEntity = categoryRepository.findById(7L).get();
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
        .password("asdsdf")
        .build());

    Long commentParentId = commentRepository.findAll().get(0).getParentId();
    commentEntity = CommentEntity.builder()
        .content(content)
        .registerTime(registerTime)
        .updateTime(updateTime)
        .ipAddress(ipAddress)
        .likeCount(likeCount)
        .dislikeCount(dislikeCount)
        .parentId(commentParentId)
//        .memberId(memberId)
        .postingId(posting)
        .build();

  }

  @Test
  public void createTest() {
    commentRepository.save(commentEntity);
    LOGGER.info("insert할 comment의 id : " + commentEntity.getId().toString());
    commentRepository.findAll().forEach(comment -> LOGGER.info(comment.getId()));
  }

  @Test
  public void findTest() {
    Optional<CommentEntity> commentSelect = commentRepository.findById(550L);
    Assertions.assertTrue(commentSelect.isPresent());
  }

  @Test
  public void viewAllTest() {
    commentRepository.findAll().forEach(comment -> LOGGER.info(comment.getId()));
  }

  @Test
  public void updateTest() {
    Long updateId = 550L;
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
    commentRepository.deleteById(550L);
    Assertions.assertTrue(!commentRepository.findById(550L).isPresent());
  }

}
