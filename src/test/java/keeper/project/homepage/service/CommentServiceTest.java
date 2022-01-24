package keeper.project.homepage.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.exception.CustomCommentNotFoundException;
import keeper.project.homepage.repository.CategoryRepository;
import keeper.project.homepage.repository.CommentRepository;
import keeper.project.homepage.repository.MemberRepository;
import keeper.project.homepage.repository.PostingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class CommentServiceTest {

  private static final Logger LOGGER = LogManager.getLogger(CommentServiceTest.class);

  private CommentEntity commentEntity;

  private MemberEntity memberEntity;

  @Autowired
  private CommentService commentService;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private PostingRepository postingRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private MemberRepository memberRepository;

  private String content = "댓글 내용";
  private LocalDate registerTime = LocalDate.now();
  private LocalDate updateTime = LocalDate.now();
  private String ipAddress = "127.0.0.1";
  private Integer likeCount = 0;
  private Integer dislikeCount = 0;

  @BeforeEach
  public void setup() throws Exception {
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

    PostingEntity postingEntity = postingRepository.save(PostingEntity.builder()
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

    CommentEntity parentComment = commentRepository.save(CommentEntity.builder()
        .content("부모 댓글 내용")
        .registerTime(registerTime)
        .updateTime(updateTime)
        .ipAddress(ipAddress)
        .likeCount(likeCount)
        .dislikeCount(dislikeCount)
        .parentId(0L)
        .memberId(memberEntity)
        .postingId(postingEntity)
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
        .postingId(postingEntity)
        .build());

  }


  @Test
  @DisplayName("댓글 생성")
  public void createTest() {
    Long befCnt = commentRepository.count();
    commentRepository.findAll().forEach(comment -> LOGGER.info("bef id list: " + comment.getId()));
    commentService.save(CommentEntity.builder().content("댓글 내용")
        .registerTime(registerTime)
        .updateTime(updateTime)
        .ipAddress(ipAddress)
        .likeCount(likeCount)
        .dislikeCount(dislikeCount)
        .parentId(commentEntity.getId())
        .memberId(memberEntity)
        .postingId(commentEntity.getPostingId())
        .build());
    Long aftCnt = commentRepository.count();
    commentRepository.findAll().forEach(comment -> LOGGER.info("aft id list: " + comment.getId()));
    Assertions.assertEquals(befCnt + 1, aftCnt);
  }

  @Test
  @DisplayName("댓글 리스트")
  public void findAllTest() {
    List<CommentEntity> commentEntityList = commentService.commentViewAll();
    commentEntityList.forEach(comment -> LOGGER.info(comment.getId()));
  }

  @Test
  @DisplayName("댓글 페이징")
  public void findAllWithPagingTest() {
    PostingEntity posting = commentEntity.getPostingId();
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    Page<CommentEntity> commentEntityPage = commentService.findAllByPost(posting, pageable);
    commentEntityPage.forEach(comment -> LOGGER.info(comment.getId()));
  }

  @Test
  @DisplayName("댓글 수정")
  public void updateTest() throws RuntimeException {
    Long updateId = commentRepository.findAll().get(0).getId();
    String updateString = "MMMMMoDDDDDiFFFFFy;";

    CommentEntity original = commentRepository.findAll().get(0);
    CommentEntity temp = CommentEntity.builder()
        .content(updateString).likeCount(original.getLikeCount())
        .dislikeCount(original.getDislikeCount()).updateTime(LocalDate.now())
        .ipAddress(original.getIpAddress()).build();
    original.changeProperties(temp);

    CommentEntity afterUpdate = commentService.updateById(updateId, original);
    Assertions.assertEquals(afterUpdate.getId(), updateId);
    Assertions.assertEquals(afterUpdate.getContent(), updateString);
    LOGGER.info("업데이트 시간 : " + afterUpdate.getUpdateTime());
  }

  @Test
  @DisplayName("댓글 조회")
  public void findByIdTest() throws RuntimeException {
    Long findId = commentEntity.getId();
    CommentEntity findComment = commentService.findById(findId);
    Assertions.assertNotNull(findComment);
    Assertions.assertEquals(findComment.getId(), findId);
  }

  @Test
  @DisplayName("댓글 삭제")
  public void deleteTest() throws RuntimeException {
    Long deleteId = commentEntity.getId();
    commentService.deleteById(deleteId);
    Assertions.assertThrows(CustomCommentNotFoundException.class,
        () -> commentService.findById(deleteId));
  }

//  @Test
//  @DisplayName("좋아요 체크")
//  public void increaseLikeCountTest() {
//    Integer originLikeCount = commentService.findById(1L).getLikeCount();
//    Integer afterLikeCount = commentService.increaseLikeCount(1L).getLikeCount();
//    Assertions.assertEquals(afterLikeCount, originLikeCount + 1);
//  }
//
//  @Test
//  @DisplayName("좋아요 취소")
//  public void decreaseLikeCountTest() {
//    Integer originLikeCount = commentService.findById(1L).getLikeCount();
//    Integer afterLikeCount = commentService.decreaseLikeCount(1L).getLikeCount();
//    Assertions.assertEquals(afterLikeCount, originLikeCount - 1);
//  }
//
//  @Test
//  @DisplayName("싫어요 체크")
//  public void increaseDislikeCountTest() {
//    Integer originDislikeCount = commentService.findById(1L).getDislikeCount();
//    Integer afterDislikeCount = commentService.increaseDislikeCount(1L).getDislikeCount();
//    Assertions.assertEquals(afterDislikeCount, originDislikeCount + 1);
//  }
//
//  @Test
//  @DisplayName("싫어요 취소")
//  public void decreaseDislikeCountTest() {
//    Integer originDislikeCount = commentService.findById(1L).getDislikeCount();
//    Integer afterDislikeCount = commentService.decreaseDislikeCount(1L).getDislikeCount();
//    Assertions.assertEquals(afterDislikeCount, originDislikeCount - 1);
//  }
}
