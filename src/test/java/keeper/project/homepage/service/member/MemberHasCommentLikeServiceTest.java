package keeper.project.homepage.service.member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
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
public class MemberHasCommentLikeServiceTest extends ApiControllerTestHelper {

  @Autowired
  private MemberHasCommentLikeRepository memberHasCommentLikeRepository;

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
    memberEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);

    CategoryEntity categoryEntity = generateCategoryEntity();

    PostingEntity posting = generatePostingEntity(memberEntity, categoryEntity, 0, 0, 0);

    parentComment = generateCommentEntity(posting, memberEntity, 1L);

    commentEntity = generateCommentEntity(posting, memberEntity, parentComment.getId());

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
