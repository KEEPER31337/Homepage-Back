package keeper.project.homepage.admin.controller.posting;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.common.entity.posting.CategoryEntity;
import keeper.project.homepage.common.entity.posting.CommentEntity;
import keeper.project.homepage.common.entity.posting.PostingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AdminCommentControllerTest extends ApiControllerTestHelper {

  private PostingEntity postingEntity;
  private CommentEntity commentEntity;
  private CommentEntity replyEntity;
  private MemberEntity userEntity;
  private MemberEntity adminEntity;

  private String userToken;
  private String adminToken;

  @BeforeEach
  public void setUp() throws Exception {
    userEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(userEntity);
    adminEntity = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(adminEntity);

    CategoryEntity categoryEntity = generateCategoryEntity();
    postingEntity = generatePostingEntity(userEntity, categoryEntity, 0, 1, 0);
    commentEntity = generateCommentEntity(postingEntity, userEntity, 0L);
    replyEntity = generateCommentEntity(postingEntity, userEntity, commentEntity.getId());
  }

  @Test
  @DisplayName("관리자 권한 댓글 삭제 - 성공")
  public void adminCommentDeleteTest() throws Exception {
    String docSuccess = "성공: true +\n실패: false";
    String docCode =
        "존재하지 않는 댓글인 경우: " + exceptionAdvice.getMessage("commentNotFound.code") + " +\n"
            + "삭제 중 에러가 난 경우: " + exceptionAdvice.getMessage("unKnown.code");
    String docMsg = "댓글 기록을 완전히 삭제하지 않고 작성자와 댓글 내용, 좋아요와 싫어요 수를 초기화합니다.";
    Long commentId = replyEntity.getId();
    mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/v1/admin/comment/{commentId}", commentId)
                .header("Authorization", adminToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("admin-comment-delete",
            pathParameters(
                parameterWithName("commentId").description("삭제할 댓글의 id")
            ),
            responseFields(
                generateCommonResponseFields(docSuccess, docCode, docMsg)
            )
        ));
  }
}
