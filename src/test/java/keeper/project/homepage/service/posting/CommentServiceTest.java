package keeper.project.homepage.service.posting;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.dto.posting.CommentDto;
import keeper.project.homepage.entity.member.MemberHasCommentEntityPK;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.exception.CustomCommentNotFoundException;
import keeper.project.homepage.repository.member.MemberHasCommentDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasCommentLikeRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.repository.posting.CommentRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.posting.PostingRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@Log4j2
public class CommentServiceTest {

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

  @Autowired
  private MemberHasCommentLikeRepository memberHasCommentLikeRepository;

  @Autowired
  private MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;

  private CommentEntity commentEntity;
  private MemberEntity memberEntity;
  private PostingEntity postingEntity;

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

    postingEntity = postingRepository.save(PostingEntity.builder()
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
        .password("pw")
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
    CommentDto commentDto = new CommentDto();
    commentDto.setContent("댓글 내용");
    commentDto.setIpAddress("111.111.111.111");
    commentDto.setMemberId(memberEntity.getId());

    CommentDto createDto = commentService.save(commentDto, postingEntity.getId());
    Assertions.assertNotNull(createDto.getId());
  }

  @Test
  @DisplayName("댓글 페이징")
  public void findAllWithPagingTest() {
    Long postId = postingEntity.getId();
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    List<CommentDto> commentEntityPage = commentService.findAllByPost(postId, pageable);

    Assertions.assertFalse(commentEntityPage.isEmpty());
    commentEntityPage.forEach(comment -> log.info(comment.getId()));
  }

  @Test
  @DisplayName("댓글 수정")
  public void updateTest() throws RuntimeException {
    CommentDto commentDto = new CommentDto();
    commentDto.setContent("수정한 댓글 내용");
    commentDto.setIpAddress("111.111.111.111");
    commentDto.setMemberId(memberEntity.getId());

    Long updateId = commentEntity.getId();

    CommentDto updateDto = commentService.updateById(commentDto, updateId);
    Assertions.assertNotNull(updateDto.getId());
    Assertions.assertEquals(updateDto.getContent(), "수정한 댓글 내용");
  }

  @Test
  @DisplayName("댓글 조회")
  public void findByIdTest() throws RuntimeException {
    Long findId = commentEntity.getId();
    CommentEntity findComment = commentService.findById(findId);
    Assertions.assertNotNull(findComment);
  }

  @Test
  @DisplayName("댓글 삭제")
  public void deleteTest() throws RuntimeException {
    Long deleteId = commentEntity.getId();
    commentService.deleteById(deleteId);
    Assertions.assertTrue(commentRepository.findById(deleteId).isEmpty());
  }

  @Test
  @DisplayName("좋아요 추가 및 취소")
  public void increaseLikeCountTest() {
    Integer originLikeCount = commentEntity.getLikeCount();

    // 좋아요 추가
    commentService.updateLikeCount(memberEntity.getId(), commentEntity.getId());
    Integer addLikeCount = commentEntity.getLikeCount();
    Assertions.assertTrue(memberHasCommentLikeRepository.findById(
        new MemberHasCommentEntityPK(memberEntity, commentEntity)).isPresent());
    Assertions.assertEquals(addLikeCount, originLikeCount + 1);

    // 좋아요 취소
    commentService.updateLikeCount(memberEntity.getId(), commentEntity.getId());
    Integer cancelLikeCount = commentEntity.getLikeCount();
    Assertions.assertTrue(memberHasCommentLikeRepository.findById(
        new MemberHasCommentEntityPK(memberEntity, commentEntity)).isEmpty());
    Assertions.assertEquals(cancelLikeCount, originLikeCount);
  }

  @Test
  @DisplayName("싫어요 추가 및 취소")
  public void increaseDislikeCountTest() {
    Integer originDislikeCount = commentEntity.getDislikeCount();

    // 싫어요 추가
    commentService.updateDislikeCount(memberEntity.getId(), commentEntity.getId());
    Integer addDislikeCount = commentEntity.getDislikeCount();
    Assertions.assertTrue(memberHasCommentDislikeRepository.findById(
        new MemberHasCommentEntityPK(memberEntity, commentEntity)).isPresent());
    Assertions.assertEquals(addDislikeCount, originDislikeCount + 1);

    // 싫어요 취소
    commentService.updateDislikeCount(memberEntity.getId(), commentEntity.getId());
    Integer cancelDislikeCount = commentEntity.getDislikeCount();
    Assertions.assertTrue(memberHasCommentDislikeRepository.findById(
        new MemberHasCommentEntityPK(memberEntity, commentEntity)).isEmpty());
    Assertions.assertEquals(cancelDislikeCount, originDislikeCount);
  }
}
