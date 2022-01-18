package keeper.project.homepage.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import keeper.project.homepage.repository.PostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PostingControllerTest {

  @Autowired
  private PostingRepository postingRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext ctx;

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
        .build();
  }

  @Test
  public void findAllPosting() throws Exception {

    ResultActions result = mockMvc.perform(
        get("/v1/post/latest")                 // (2)
            .param("page", "0")
            .param("size", "5")
            .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getLatest",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                fieldWithPath("[].id").description("게시물 ID"),
                fieldWithPath("[].title").description("게시물 제목"),
                fieldWithPath("[].content").description("게시물 내용"),
                fieldWithPath("[].memberId").description("멤버 ID"),
                fieldWithPath("[].visitCount").description("조회 수"),
                fieldWithPath("[].likeCount").description("좋아요 수"),
                fieldWithPath("[].dislikeCount").description("싫어요 수"),
                fieldWithPath("[].commentCount").description("댓글 수"),
                fieldWithPath("[].registerTime").description("작성 시간"),
                fieldWithPath("[].updateTime").description("수정 시간"),
                fieldWithPath("[].ipAddress").description("IP 주소"),
                fieldWithPath("[].allowComment").description("댓글 허용?"),
                fieldWithPath("[].isNotice").description("공지글?"),
                fieldWithPath("[].isSecret").description("비밀글?"),
                fieldWithPath("[].password").description("비밀번호")
            )
        ));
  }

  @Test
  public void findAllPostingByCategoryId() throws Exception {

    ResultActions result = mockMvc.perform(get("/v1/post/lists")
        .param("page", "0")
        .param("size", "5")
        .param("category", "6")
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getList",
            requestParameters(
                parameterWithName("category").description("게시판 종류 ID"),
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                fieldWithPath("[].id").description("게시물 ID"),
                fieldWithPath("[].title").description("게시물 제목"),
                fieldWithPath("[].content").description("게시물 내용"),
                fieldWithPath("[].memberId").description("멤버 ID"),
                fieldWithPath("[].visitCount").description("조회 수"),
                fieldWithPath("[].likeCount").description("좋아요 수"),
                fieldWithPath("[].dislikeCount").description("싫어요 수"),
                fieldWithPath("[].commentCount").description("댓글 수"),
                fieldWithPath("[].registerTime").description("작성 시간"),
                fieldWithPath("[].updateTime").description("수정 시간"),
                fieldWithPath("[].ipAddress").description("IP 주소"),
                fieldWithPath("[].allowComment").description("댓글 허용?"),
                fieldWithPath("[].isNotice").description("공지글?"),
                fieldWithPath("[].isSecret").description("비밀글?"),
                fieldWithPath("[].password").description("비밀번호")
            )
        ));
  }

  @Test
  public void getPosting() throws Exception {
    Integer postingId = 44;
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/{pid}", postingId));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getOne",
            pathParameters(
                parameterWithName("pid").description("게시물 ID")
            ),
            responseFields(
                fieldWithPath("id").description("게시물 ID"),
                fieldWithPath("title").description("게시물 제목"),
                fieldWithPath("content").description("게시물 내용"),
                fieldWithPath("memberId").description("멤버 ID"),
                fieldWithPath("visitCount").description("조회 수"),
                fieldWithPath("likeCount").description("좋아요 수"),
                fieldWithPath("dislikeCount").description("싫어요 수"),
                fieldWithPath("commentCount").description("댓글 수"),
                fieldWithPath("registerTime").description("작성 시간"),
                fieldWithPath("updateTime").description("수정 시간"),
                fieldWithPath("ipAddress").description("IP 주소"),
                fieldWithPath("allowComment").description("댓글 허용?"),
                fieldWithPath("isNotice").description("공지글?"),
                fieldWithPath("isSecret").description("비밀글?"),
                fieldWithPath("password").description("비밀번호")
            )
        ));
  }

  @Test
  public void getAttachList() throws Exception {
    Integer postingId = 47;
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/attach/{pid}", postingId));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-getAttachList",
            pathParameters(
                parameterWithName("pid").description("게시물 ID")
            ),
            responseFields(
                fieldWithPath("[].id").description("첨부파일 ID"),
                fieldWithPath("[].fileName").description("첨부파일 이름"),
                fieldWithPath("[].filePath").description("첨부파일 경로(상대경로)"),
                fieldWithPath("[].fileSize").description("첨부파일 크기"),
                fieldWithPath("[].uploadTime").description("업로드 시간"),
                fieldWithPath("[].ipAddress").description("IP 주소")
            )
        ));
  }

  @Test
  public void createPosting() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    params.add("title", "mvc제목");
    params.add("content", "mvc내용");
    params.add("memberId", "3");
    params.add("categoryId", "7");
    params.add("ipAddress", "192.111.222");
    params.add("allowComment", "0");
    params.add("isNotice", "0");
    params.add("isSecret", "1");
    params.add("password", "asd");

    ResultActions result = mockMvc.perform(
        multipart("/v1/post/new")
            .file(file)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("POST");
              return request;
            }));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-create",
            requestParameters(
                parameterWithName("title").description("제목"),
                parameterWithName("content").description("내용"),
                parameterWithName("memberId").description("멤버 ID"),
                parameterWithName("categoryId").description("게시판 종류 ID"),
                parameterWithName("ipAddress").description("IP 주소"),
                parameterWithName("allowComment").description("댓글 허용?"),
                parameterWithName("isNotice").description("공지글?"),
                parameterWithName("isSecret").description("비밀글?"),
                parameterWithName("password").optional().description("비밀번호")
            ),
            requestParts(
                partWithName("file").description("첨부 파일들 (form-data 에서 file= parameter 부분)")
            )
        ));
  }

  @Test
  public void modifyPosting() throws Exception {
    Integer postingId = 45;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile file = new MockMultipartFile("file", "modifyImage.png", "image/png",
        "<<png data>>".getBytes());
    params.add("title", "수정 mvc제목");
    params.add("content", "수정 mvc내용");
    params.add("memberId", "3");
    params.add("categoryId", "7");
    params.add("ipAddress", "192.111.222");
    params.add("allowComment", "0");
    params.add("isNotice", "0");
    params.add("isSecret", "1");
    params.add("password", "asd");

    ResultActions result = mockMvc.perform(
        multipart("/v1/post/{pid}", postingId)
            .file(file)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-modify",
//            multipart() 요청에서는 pathParameter가 요청이 안됨. -> 따로 작성
//            pathParameters(
//                parameterWithName("pid").description("게시물 ID")
//            ),
            requestParameters(
                parameterWithName("title").description("제목"),
                parameterWithName("content").description("내용"),
                parameterWithName("memberId").description("멤버 ID"),
                parameterWithName("categoryId").description("게시판 종류 ID"),
                parameterWithName("ipAddress").description("IP 주소"),
                parameterWithName("allowComment").description("댓글 허용?"),
                parameterWithName("isNotice").description("공지글?"),
                parameterWithName("isSecret").description("비밀글?"),
                parameterWithName("password").optional().description("비밀번호")
            ),
            requestParts(
                partWithName("file").description("첨부 파일들 (form-data 에서 file= parameter 부분)")
            )
        ));
  }

  @Test
  public void removePosting() throws Exception {
    Integer postingId = 35;
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.delete("/v1/post/{pid}", postingId));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-delete",
            pathParameters(
                parameterWithName("pid").description("게시물 ID")
            )
        ));
  }

  @Test
  public void searchPosting() throws Exception {
    ResultActions result = mockMvc.perform(get("/v1/post/search")
        .param("type", "T")
        .param("keyword", "수정")
        .param("page", "0")
        .param("size", "5")
        .param("category", "6")
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-search",
            requestParameters(
                parameterWithName("type").description(
                    "검색 타입 (T : 제목, C: 내용, TC: 제목 또는 내용, W : 작성자)"),
                parameterWithName("keyword").description("검색어"),
                parameterWithName("category").description("게시판 종류 ID"),
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                fieldWithPath("[].id").description("게시물 ID"),
                fieldWithPath("[].title").description("게시물 제목"),
                fieldWithPath("[].content").description("게시물 내용"),
                fieldWithPath("[].memberId").description("멤버 ID"),
                fieldWithPath("[].visitCount").description("조회 수"),
                fieldWithPath("[].likeCount").description("좋아요 수"),
                fieldWithPath("[].dislikeCount").description("싫어요 수"),
                fieldWithPath("[].commentCount").description("댓글 수"),
                fieldWithPath("[].registerTime").description("작성 시간"),
                fieldWithPath("[].updateTime").description("수정 시간"),
                fieldWithPath("[].ipAddress").description("IP 주소"),
                fieldWithPath("[].allowComment").description("댓글 허용?"),
                fieldWithPath("[].isNotice").description("공지글?"),
                fieldWithPath("[].isSecret").description("비밀글?"),
                fieldWithPath("[].password").description("비밀번호")
            )
        ));
  }
}
