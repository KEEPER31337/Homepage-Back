package keeper.project.homepage.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.repository.CategoryRepository;
import keeper.project.homepage.repository.CommentRepository;
import keeper.project.homepage.repository.MemberRepository;
import keeper.project.homepage.repository.PostingRepository;
import keeper.project.homepage.service.MemberHasCommentDislikeService;
import keeper.project.homepage.service.MemberHasCommentLikeService;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Log4j2
public class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  // service로 수정
  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private WebApplicationContext ctx;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private PostingRepository postingRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private MemberHasCommentLikeService memberHasCommentLikeService;

  @Autowired
  private MemberHasCommentDislikeService memberHasCommentDislikeService;

  private LocalDate registerTime = LocalDate.now();
  private LocalDate updateTime = LocalDate.now();
  private String ipAddress = "127.0.0.1";
  private Integer likeCount = 1000;
  private Integer dislikeCount = 100;

  private CommentEntity commentEntity;
  private MemberEntity memberEntity;

  @BeforeEach
  public void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {
    memberEntity = memberRepository.save(
        MemberEntity.builder()
            .loginId("로그인")
            .password("비밀번호")
            .realName("이름")
            .nickName("닉네임")
            .emailAddress("이메일")
            .studentId("학번")
            .roles(Collections.singletonList("ROLE_USER")).build());

    // mockMvc의 한글 사용을 위한 코드
    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
        .apply(documentationConfiguration(restDocumentation)
            .operationPreprocessors()
            .withRequestDefaults(modifyUris().host("test.com").removePort(), prettyPrint())
            .withResponseDefaults(prettyPrint())
        )
        .alwaysDo(print())
        .build();

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
        .likeCount(10)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asdsdf")
        .memberId(memberEntity)
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
  @DisplayName("댓글 생성")
  public void commentCreateTest() throws Exception {
    Long commentParentId = commentRepository.findAll().get(0).getParentId();
    Long memberId = memberEntity.getId();
    String content = "{\n"
        + "    \"content\": \"SDFASFASG\",\n"
        + "    \"ipAddress\": \"672.523.937.636\",\n"
        + "    \"likeCount\": 9,\n"
        + "    \"dislikeCount\": 0,\n"
        + "    \"parentId\": " + commentParentId + ",\n"
        + "    \"memberId\": " + memberId + "\n"
        + "}";
    Long postId = postingRepository.findAll().get(0).getId();

    mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/comment/{postId}", postId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("comment-create",
            pathParameters(
                parameterWithName("postId").description("댓글을 추가할 게시글의 id")
            ),
            requestFields(
                fieldWithPath("content").description("댓글 내용"),
                fieldWithPath("ipAddress").description("댓글 작성자의 ip address"),
                fieldWithPath("likeCount").description("좋아요 개수"),
                fieldWithPath("dislikeCount").description("싫어요 개수"),
                fieldWithPath("parentId").description("대댓글인 경우, 부모 댓글의 id"),
                fieldWithPath("memberId").description("댓글 작성자의 member id")
            ),
            responseFields(
                fieldWithPath("success").description(""),
                fieldWithPath("data").description(""),
                fieldWithPath("msg").description(""),
                fieldWithPath("code").description("")
            )));
  }

  @Test
  @DisplayName("댓글 페이징")
  public void findCommentByPostIdTest() throws Exception {
    Long postId = postingRepository.findAll().get(0).getId();

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/v1/comment/{postId}", postId)
                .param("page", "0")
                .param("size", "20")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("comment-list",
            pathParameters(
                parameterWithName("postId").description("조회할 댓글들이 포함된 게시글의 id")
            ),
            requestParameters(
                parameterWithName("page").description("페이지 번호 (페이지 시작 번호 : 0)"),
                parameterWithName("size").description("한 페이지에 들어갈 댓글 개수 (default : 10)")
            ),
            responseFields(
                fieldWithPath("success").description(""),
                fieldWithPath("code").description(""),
                fieldWithPath("msg").description(""),
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
  @DisplayName("댓글 삭제")
  public void commentDeleteTest() throws Exception {
    Long commentId = commentRepository.findAll().get(0).getId();
    mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/comment/{commentId}", commentId))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("comment-delete",
            pathParameters(
                parameterWithName("commentId").description("삭제할 댓글의 id")
            ),
            responseFields(
                fieldWithPath("success").description(""),
                fieldWithPath("code").description(""),
                fieldWithPath("msg").description("")
            )
        ));
  }

  @Test
  @DisplayName("댓글 수정")
  public void commentUpdateTest() throws Exception {
    Long updateId = commentRepository.findAll().get(0).getId();
    String updateContent = "{\n"
        + "    \"content\": \"수정한 내용!\"\n"
        + "}";
    log.info("수정 전 내용 : " + commentRepository.findById(updateId).get().toString());
    mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/comment/{commentId}", updateId)
            .content(updateContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("comment-update",
            pathParameters(
                parameterWithName("commentId").description("수정할 댓글의 id")
            ),
            responseFields(
                fieldWithPath("success").description(""),
                fieldWithPath("code").description(""),
                fieldWithPath("msg").description(""),
                fieldWithPath("data").description("")
            )));
    log.info("수정 후 내용 : " + commentRepository.findById(updateId).get().toString());
  }

  @Test
  @DisplayName("댓글 좋아요 추가 및 취소")
  public void updateLikeTest() throws Exception {
    Long commentId = commentEntity.getId();
    Integer befLikeCount = commentEntity.getLikeCount();
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
                fieldWithPath("success").description(""),
                fieldWithPath("code").description(""),
                fieldWithPath("msg").description(""),
                fieldWithPath("data").description("")
            )));
    Integer addLikeCount = commentRepository.findById(commentId).get().getLikeCount();
    Assertions.assertNotNull(memberHasCommentLikeService.findById(memberEntity, commentEntity));
    Assertions.assertEquals(befLikeCount + 1, addLikeCount);

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
                fieldWithPath("success").description(""),
                fieldWithPath("code").description(""),
                fieldWithPath("msg").description(""),
                fieldWithPath("data").description("")
            )));
    Integer addDislikeCount = commentRepository.findById(commentId).get().getDislikeCount();
    Assertions.assertNotNull(memberHasCommentDislikeService.findById(memberEntity, commentEntity));
    Assertions.assertEquals(befDislikeCount + 1, addDislikeCount);

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
