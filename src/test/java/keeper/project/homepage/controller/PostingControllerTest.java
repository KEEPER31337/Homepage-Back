package keeper.project.homepage.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Collections;
import java.util.Date;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.OriginalImageEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
@Log4j2
public class PostingControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";

  private MemberEntity memberEntity;
  private CategoryEntity categoryEntity;
  private PostingEntity postingEntity;
  private ThumbnailEntity thumbnailEntity1;
  private OriginalImageEntity originalImageEntity1;
  private ThumbnailEntity thumbnailEntity2;
  private OriginalImageEntity originalImageEntity2;

  @BeforeEach
  public void setUp() throws Exception {
    memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName("test작성자")
        .emailAddress(emailAddress)
        .studentId(studentId)
        .roles(Collections.singletonList("ROLE_USER"))
        .build();
    memberRepository.save(memberEntity);

    categoryEntity = CategoryEntity.builder()
        .name("테스트 게시판").build();
    categoryRepository.save(categoryEntity);

    originalImageEntity1 = OriginalImageEntity.builder().path("files/image_1.jpg").build();
    originalImageRepository.save(originalImageEntity1);

    thumbnailEntity1 = ThumbnailEntity.builder().path("files/t_image_1.jpg")
        .originalImage(originalImageEntity1).build();
    thumbnailRepository.save(thumbnailEntity1);

    originalImageEntity2 = OriginalImageEntity.builder().path("files/image_2.jpg").build();
    originalImageRepository.save(originalImageEntity2);

    thumbnailEntity2 = ThumbnailEntity.builder().path("files/t_image_2.jpg")
        .originalImage(originalImageEntity2).build();
    thumbnailRepository.save(thumbnailEntity2);

    postingEntity = PostingEntity.builder()
        .title("test 게시판 제목")
        .content("test 게시판 제목 내용")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnailId(thumbnailEntity1)
        .ipAddress("192.11.222.333")
        .allowComment(0)
        .isNotice(0)
        .isSecret(1)
        .likeCount(0)
        .dislikeCount(0)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asd")
        .build();

    postingRepository.save(postingEntity);
    postingRepository.save(PostingEntity.builder()
        .title("test 게시판 제목2")
        .content("test 게시판 제목 내용2")
        .memberId(memberEntity)
        .categoryId(categoryEntity)
        .thumbnailId(thumbnailEntity2)
        .ipAddress("192.11.223")
        .allowComment(0)
        .isNotice(0)
        .isSecret(1)
        .likeCount(0)
        .dislikeCount(1)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asd2")
        .build());

    fileRepository.save(FileEntity.builder()
        .postingId(postingEntity)
        .fileName("test file")
        .filePath("test/file.txt")
        .fileSize(12345L)
        .uploadTime(new Date())
        .ipAddress(postingEntity.getIpAddress())
        .build());
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
                fieldWithPath("[].writer").optional().description("작성자"),
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
        .param("category", categoryEntity.getId().toString())
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
                fieldWithPath("[].writer").description("작성자"),
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
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/{pid}", postingEntity.getId()));

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
                fieldWithPath("writer").description("작성자"),
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
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/post/attach/{pid}",
            postingEntity.getId().toString()));

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
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", "image_1.jpg", "image/jpg",
        "<<jpg data>>".getBytes());
    params.add("title", "mvc제목");
    params.add("content", "mvc내용");
    params.add("memberId", memberEntity.getId().toString());
    params.add("categoryId", categoryEntity.getId().toString());
    params.add("ipAddress", "192.111.222");
    params.add("allowComment", "0");
    params.add("isNotice", "0");
    params.add("isSecret", "1");
    params.add("password", "asd");

    ResultActions result = mockMvc.perform(
        multipart("/v1/post/new")
            .file(file)
            .file(thumbnail)
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
                partWithName("file").description("첨부 파일들 (form-data 에서 file= parameter 부분)"),
                partWithName("thumbnail").description(
                    "썸네일 용 이미지 (form-data 에서 thumbnail= parameter 부분)")
            )
        ));
  }

  @Test
  public void modifyPosting() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile file = new MockMultipartFile("file", "modifyImage.png", "image/png",
        "<<png data>>".getBytes());
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", "image_2.jpg", "image/jpg",
        "<<jpg data>>".getBytes());
    params.add("title", "수정 mvc제목");
    params.add("content", "수정 mvc내용");
    params.add("memberId", memberEntity.getId().toString());
    params.add("categoryId", categoryEntity.getId().toString());
    params.add("thumbnailId", thumbnailEntity1.getId().toString());
    params.add("ipAddress", "192.111.222");
    params.add("allowComment", "0");
    params.add("isNotice", "0");
    params.add("isSecret", "1");
    params.add("password", "asd");

    log.info("mockMVc 시작");
    ResultActions result = mockMvc.perform(
        multipart("/v1/post/{pid}", postingEntity.getId().toString())
            .file(file)
            .file(thumbnail)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }));

    log.info("mockMVc 결과");
    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-modify",
//            multipart() 요청에서는 pathParameter가 요청이 안됨. -> adoc에서 따로 작성
//            pathParameters(
//                parameterWithName("pid").description("게시물 ID")
//            ),
            requestParameters(
                parameterWithName("title").description("제목"),
                parameterWithName("content").description("내용"),
                parameterWithName("memberId").description("멤버 ID"),
                parameterWithName("categoryId").description("게시판 종류 ID"),
                parameterWithName("thumbnailId").description("썸네일 ID"),
                parameterWithName("ipAddress").description("IP 주소"),
                parameterWithName("allowComment").description("댓글 허용?"),
                parameterWithName("isNotice").description("공지글?"),
                parameterWithName("isSecret").description("비밀글?"),
                parameterWithName("password").optional().description("비밀번호")
            ),
            requestParts(
                partWithName("file").description("첨부 파일들 (form-data 에서 file= parameter 부분)"),
                partWithName("thumbnail").description(
                    "썸네일 용 이미지 (form-data 에서 thumbnail= parameter 부분)")
            )
        ));
  }

  @Test
  public void removePosting() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.delete("/v1/post/{pid}",
            postingEntity.getId().toString()));

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
        .param("keyword", "2")
        .param("page", "0")
        .param("size", "5")
        .param("category", categoryEntity.getId().toString())
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
                fieldWithPath("[].writer").description("작성자"),
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
  @Transactional
  public void likePosting() throws Exception {

    ResultActions result = mockMvc.perform(get("/v1/post/like")
        .param("memberId", memberEntity.getId().toString())
        .param("postingId", postingEntity.getId().toString())
        .param("type", "INC")
        .contentType(MediaType.APPLICATION_JSON));

//    result.andDo(print());

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-like",
            requestParameters(
                parameterWithName("type").description("타입 (INC : 좋아요 +, DEC : 좋아요 -)"),
                parameterWithName("memberId").description("멤버 ID"),
                parameterWithName("postingId").description("게시판 ID")
            )
        ));
  }

  @Test
  public void dislikePosting() throws Exception {
    ResultActions result = mockMvc.perform(get("/v1/post/dislike")
        .param("memberId", memberEntity.getId().toString())
        .param("postingId", postingEntity.getId().toString())
        .param("type", "INC")
        .contentType(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("post-dislike",
            requestParameters(
                parameterWithName("type").description("타입 (INC : 싫어요 +, DEC : 싫어요 -"),
                parameterWithName("memberId").description("멤버 ID"),
                parameterWithName("postingId").description("게시판 ID")
            )
        ));
  }
}
