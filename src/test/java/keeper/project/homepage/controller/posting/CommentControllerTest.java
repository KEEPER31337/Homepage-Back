package keeper.project.homepage.controller.posting;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class CommentControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";

  private LocalDate registerTime = LocalDate.now();
  private LocalDate updateTime = LocalDate.now();
  private String ipAddress = "127.0.0.1";
  private Integer likeCount = 0;
  private Integer dislikeCount = 0;

  private PostingEntity postingEntity;
  private CommentEntity parentComment;
  private CommentEntity commentEntity;
  private MemberEntity memberEntity;

  @BeforeEach
  public void setUp() throws Exception {
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
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asdsdf")
        .memberId(memberEntity)
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
  public void commentCreateTest() throws Exception {
    Long commentParentId = parentComment.getId();
    Long memberId = memberEntity.getId();
    Long postId = postingEntity.getId();
    String content = "{\n"
        + "    \"content\": \"SDFASFASG\",\n"
        + "    \"ipAddress\": \"672.523.937.636\",\n"
        + "    \"parentId\": \n" + commentParentId + ",\n"
        + "    \"memberId\": " + memberId + "\n"
        + "}";

    mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/comment/{postId}", postId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(document("comment-create",
            pathParameters(
                parameterWithName("postId").description("댓글을 추가할 게시글의 id")
            ),
            requestFields(
                fieldWithPath("content").description("댓글 내용"),
                fieldWithPath("ipAddress").description("댓글 작성자의 ip address"),
                fieldWithPath("parentId").optional()
                    .description("모댓글: 입력 없음(db에는 0으로 설정), 대댓글: 부모 댓글의 id"),
                fieldWithPath("memberId").description("댓글 작성자의 member id")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("content가 비어있을 경우: BAD_REQUEST(400)"),
                fieldWithPath("msg").description("content가 비어있을 경우: \"댓글의 내용이 비어있습니다.\"")
            )));
  }

  @Test
  @DisplayName("댓글 생성 실패 - content가 비어있는 경우")
  public void commentCreateFailTest() throws Exception {
    Long commentParentId = parentComment.getId();
    Long memberId = memberEntity.getId();
    Long postId = postingEntity.getId();
    String content = "{\n"
        + "    \"content\": \"\",\n"
        + "    \"ipAddress\": \"672.523.937.636\",\n"
        + "    \"parentId\": \n" + commentParentId + ",\n"
        + "    \"memberId\": " + memberId + "\n"
        + "}";

    mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/comment/{postId}", postId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
        .andDo(document("comment-create-fail"));
  }

  @Test
  @DisplayName("댓글 페이징")
  public void findCommentByPostIdTest() throws Exception {
    Long postId = postingEntity.getId();
    for (int i = 0; i < 15; i++) {
      commentRepository.save(CommentEntity.builder()
          .content("페이징 댓글 내용")
          .registerTime(registerTime)
          .updateTime(updateTime)
          .ipAddress(ipAddress)
          .likeCount(likeCount)
          .dislikeCount(dislikeCount)
          .parentId(commentEntity.getId())
          .memberId(memberEntity)
          .postingId(postingEntity)
          .build());
    }

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/v1/comment/{postId}", postId)
                .param("page", "0")
                .param("size", "10")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[?(@.list.length() == 10)]").exists()) // 0페이지의 댓글 수 10개인지 확인
        .andDo(document("comment-list",
            pathParameters(
                parameterWithName("postId").description("조회할 댓글들이 포함된 게시글의 id")
            ),
            requestParameters(
                parameterWithName("page").description("페이지 번호 (페이지 시작 번호 : 0)"),
                parameterWithName("size").description("한 페이지에 들어갈 댓글 개수 (default : 10)")
            ),
            responseFields(
                fieldWithPath("success").description("true").ignored(),
                fieldWithPath("code").description("0").ignored(),
                fieldWithPath("msg").description("성공하였습니다").ignored(),
                fieldWithPath("list[].id").description("댓글 id"),
                fieldWithPath("list[].content").description("댓글 내용"),
                fieldWithPath("list[].registerTime").description("댓글이 처음 등록된 시간"),
                fieldWithPath("list[].updateTime").description("댓글이 수정된 시간"),
                fieldWithPath("list[].ipAddress").description("댓글 작성자의 ip address"),
                fieldWithPath("list[].likeCount").description("좋아요 개수"),
                fieldWithPath("list[].dislikeCount").description("싫어요 개수"),
                fieldWithPath("list[].parentId").description("대댓글인 경우, 부모 댓글의 id"),
                fieldWithPath("list[].memberId").description("댓글 작성자의 member id"),
                fieldWithPath("list[].postingId").description("댓글이 작성된 게시글의 id"))
        ));
  }

  @Test
  @DisplayName("댓글 삭제 - 성공")
  public void commentDeleteTest() throws Exception {
    Long commentId = commentEntity.getId();
    mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/comment/{commentId}", commentId))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("comment-delete",
            pathParameters(
                parameterWithName("commentId").description("삭제할 댓글의 id")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description(
                    "이미 삭제된 댓글인 경우: BAD_REQUEST(400)" + " +\n"
                        + "삭제 중 에러가 난 경우: INTERNAL_SERVER_ERROR(500)"),
                fieldWithPath("msg").description("이미 삭제된 댓글인 경우: \"존재하지 않는 댓글입니다.\"")
            )
        ));
  }

  @Test
  @DisplayName("댓글 삭제 - 이미 존재하지 않는 댓글")
  public void commentDeleteFailTest() throws Exception {
    Long commentId = commentEntity.getId();
    commentRepository.deleteById(commentId);
    mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/comment/{commentId}", commentId))
        .andExpect(status().is4xxClientError())
        .andDo(print())
        .andDo(document("comment-delete-fail"));
  }

  @Test
  @DisplayName("댓글 수정")
  public void commentUpdateTest() throws Exception {
    Long updateId = commentEntity.getId();
    String updateString = "수정한 내용!";
    String updateContent = String.format("{\"content\": \"%s\"}", updateString);
    mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/comment/{commentId}", updateId)
            .content(updateContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[?(@.data.content == \"%s\")]", updateString).exists())
        .andDo(document("comment-update",
            pathParameters(
                parameterWithName("commentId").description("수정할 댓글의 id")
            ),
            requestFields(
                fieldWithPath("content").description("수정할 댓글 내용")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("commentId가 존재하지 않는 경우: BAD_REQUEST(400)" + " +\n"
                    + "content가 비어있을 경우: BAD_REQUEST(400)"),
                fieldWithPath("msg").description("content가 비어있을 경우: \"댓글의 내용이 비어있습니다.\""),
                fieldWithPath("data.id").description("수정한 댓글 id"),
                fieldWithPath("data.content").description("수정한 댓글 내용"),
                fieldWithPath("data.registerTime").ignored(),
                fieldWithPath("data.updateTime").description("수정한 시간"),
                fieldWithPath("data.ipAddress").description("수정한 댓글 작성자의 ipAdress"),
                fieldWithPath("data.likeCount").ignored(),
                fieldWithPath("data.dislikeCount").ignored(),
                fieldWithPath("data.parentId").ignored(),
                fieldWithPath("data.memberId").ignored(),
                fieldWithPath("data.postingId").ignored()
            )));
  }

  @Test
  @DisplayName("댓글 수정 실패 - content가 비어있는 경우")
  public void commentUpdateFailTest() throws Exception {
    Long updateId = commentEntity.getId();
    String updateContent = "{\"content\": \"\"}";
    mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/comment/{commentId}", updateId)
            .content(updateContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andDo(document("comment-update-fail"));
  }

  @Test
  @DisplayName("댓글 좋아요 추가 및 취소")
  public void updateLikeTest() throws Exception {
    Long commentId = commentEntity.getId();
    Integer befLikeCount = commentEntity.getLikeCount();
    // 좋아요 추가
    mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/comment/like")
            .param("commentId", commentEntity.getId().toString())
            .param("memberId", memberEntity.getId().toString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("comment-like",
            requestParameters(
                parameterWithName("commentId").description("좋아요 추가 또는 취소할 댓글의 id"),
                parameterWithName("memberId").description("좋아요 추가 또는 취소를 수행하는 멤버의 id")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true"),
                fieldWithPath("code").description("").ignored(),
                fieldWithPath("msg").description("").ignored()
            )));
    Integer addLikeCount = commentRepository.findById(commentId).get().getLikeCount();
    Assertions.assertNotNull(memberHasCommentLikeService.findById(memberEntity, commentEntity));
    Assertions.assertEquals(befLikeCount + 1, addLikeCount);

    // 좋아요 취소
    mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/comment/like")
            .param("commentId", commentEntity.getId().toString())
            .param("memberId", memberEntity.getId().toString()))
        .andDo(print())
        .andExpect(status().isOk());
    Integer delLikeCount = commentRepository.findById(commentId).get().getLikeCount();
    Assertions.assertNull(memberHasCommentLikeService.findById(memberEntity, commentEntity));
    Assertions.assertEquals(addLikeCount - 1, delLikeCount);
  }

  @Test
  @DisplayName("댓글 싫어요 추가 및 취소")
  public void updateDislikeTest() throws Exception {
    Long commentId = commentEntity.getId();
    Integer befDislikeCount = commentEntity.getDislikeCount();
    // 싫어요 추가
    mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/comment/dislike")
            .param("commentId", commentEntity.getId().toString())
            .param("memberId", memberEntity.getId().toString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("comment-dislike",
            requestParameters(
                parameterWithName("commentId").description("싫어요 추가 또는 취소할 댓글의 id"),
                parameterWithName("memberId").description("싫어요 추가 또는 취소를 수행하는 멤버의 id")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true"),
                fieldWithPath("code").description("").ignored(),
                fieldWithPath("msg").description("").ignored()
            )));
    Integer addDislikeCount = commentRepository.findById(commentId).get().getDislikeCount();
    Assertions.assertNotNull(memberHasCommentDislikeService.findById(memberEntity, commentEntity));
    Assertions.assertEquals(befDislikeCount + 1, addDislikeCount);

    // 싫어요 취소
    mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/comment/dislike")
            .param("commentId", commentEntity.getId().toString())
            .param("memberId", memberEntity.getId().toString()))
        .andDo(print())
        .andExpect(status().isOk());
    Integer delDislikeCount = commentRepository.findById(commentId).get().getDislikeCount();
    Assertions.assertNull(memberHasCommentDislikeService.findById(memberEntity, commentEntity));
    Assertions.assertEquals(addDislikeCount - 1, delDislikeCount);
  }
}
