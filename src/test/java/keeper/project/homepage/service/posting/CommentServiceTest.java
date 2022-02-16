package keeper.project.homepage.service.posting;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.dto.posting.CommentDto;
import keeper.project.homepage.entity.member.MemberHasCommentEntityPK;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberHasCommentDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasCommentLikeRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.repository.posting.CommentRepository;
import keeper.project.homepage.repository.posting.PostingRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private MemberJobRepository memberJobRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MemberHasCommentLikeRepository memberHasCommentLikeRepository;

  @Autowired
  private MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;

  private CommentEntity commentEntity;
  private MemberEntity memberEntity;
  private PostingEntity postingEntity;

  private String content = "댓글 내용";
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

  @BeforeEach
  public void setup() throws Exception {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberJobEntity)
        .build();
    memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);

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
        .registerTime(LocalDateTime.now())
        .updateTime(LocalDateTime.now())
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
        .member(memberEntity)
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
        .member(memberEntity)
        .postingId(postingEntity)
        .build());

  }


  @Test
  @DisplayName("댓글 생성")
  public void createTest() {
    CommentDto commentDto = CommentDto.builder().content("댓글 내용").ipAddress("111.111.111.111")
        .build();
    CommentDto createDto = commentService.save(commentDto, postingEntity.getId(),
        memberEntity.getId());
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
    CommentDto commentDto = CommentDto.builder()
        .content("수정한 댓글 내용")
        .build();

    Long updateId = commentEntity.getId();

    CommentDto updateDto = commentService.updateById(commentDto, updateId, memberEntity.getId());
    Assertions.assertNotNull(updateDto.getId());
    Assertions.assertEquals(updateDto.getContent(), "수정한 댓글 내용");
  }

  @Test
  @DisplayName("댓글 삭제")
  public void deleteTest() throws RuntimeException {
    Long deleteId = commentEntity.getId();
    commentService.deleteById(deleteId, memberEntity.getId());
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
