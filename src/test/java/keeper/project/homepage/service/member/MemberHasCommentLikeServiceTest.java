package keeper.project.homepage.service.member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasCommentEntityPK;
import keeper.project.homepage.entity.member.MemberHasCommentLikeEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.member.MemberHasCommentLikeRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.repository.posting.CommentRepository;
import keeper.project.homepage.repository.posting.PostingRepository;
import keeper.project.homepage.user.service.member.MemberHasCommentLikeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class MemberHasCommentLikeServiceTest {

  @Autowired
  private PasswordEncoder passwordEncoder;

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

  @Autowired
  private MemberJobRepository memberJobRepository;

  @Autowired
  private MemberHasCommentLikeService memberHasCommentLikeService;

  private CommentEntity commentEntity;
  private CommentEntity parentComment;
  private MemberEntity memberEntity;
  private MemberHasCommentLikeEntity memberHasCommentLikeEntity;

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";

  private LocalDateTime registerTime = LocalDateTime.now();
  private LocalDateTime updateTime = LocalDateTime.now();
  private String ipAddress = "127.0.0.1";
  private Integer likeCount = 0;
  private Integer dislikeCount = 0;

  @BeforeEach
  public void setup() {
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
        .generation(0F)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
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

    parentComment = commentRepository.save(CommentEntity.builder()
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

    memberHasCommentLikeEntity = memberHasCommentLikeRepository.save(
        MemberHasCommentLikeEntity.builder().memberHasCommentEntityPK(
            new MemberHasCommentEntityPK(memberEntity, commentEntity)).build());
  }

  @Test
  public void findByIdTest() {
    Assertions.assertNotNull(
        memberHasCommentLikeService.findById(memberEntity, commentEntity)
    );
    Assertions.assertNull(
        memberHasCommentLikeService.findById(memberEntity, parentComment)
    );
  }
}
