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
import java.util.Date;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.repository.CategoryRepository;
import keeper.project.homepage.repository.CommentRepository;
import keeper.project.homepage.repository.PostingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class CommentControllerTest {

  private static final Logger LOGGER = LogManager.getLogger(CommentControllerTest.class);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private WebApplicationContext ctx;

  private CommentEntity commentEntity;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private PostingRepository postingRepository;

  private String content = "댓글 내용";
  private LocalDate registerTime = LocalDate.now();
  private LocalDate updateTime = LocalDate.now();
  private String ipAddress = "127.0.0.1";
  private Integer likeCount = 1000;
  private Integer dislikeCount = 100;
  private Integer memberId = 10;

  @BeforeEach
  public void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {

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

    commentRepository.save(commentEntity);
  }

  @Test
  @DisplayName("댓글 생성")
  public void commentCreateTest() throws Exception {
    Long commentParentId = commentRepository.findAll().get(0).getParentId();
    String content = "{\n"
        + "    \"content\": \"SDFASFASG\",\n"
        + "    \"ipAddress\": \"672.523.937.636\",\n"
        + "    \"likeCount\": 9,\n"
        + "    \"dislikeCount\": 0,\n"
        + "    \"parentId\": " + commentParentId + ",\n"
        + "    \"memberId\": 1\n"
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

//    LOGGER.info(mvcResult);
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
    mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/comment/{commentId}", updateId)
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
  }
}
