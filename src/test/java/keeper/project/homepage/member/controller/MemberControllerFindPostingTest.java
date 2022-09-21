package keeper.project.homepage.controller.member;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.posting.entity.CategoryEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.posting.service.PostingService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MemberControllerFindPostingTest extends ApiControllerTestHelper {

  private String userToken;

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";

  private MemberEntity memberEntity;
  private MemberEntity memberEntity2;
  private CategoryEntity categoryEntity;
  private PostingEntity tempPosting;

  @BeforeEach
  public void setup() throws Exception {
    memberEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    memberEntity2 = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(memberEntity);

    categoryEntity = generateCategoryEntity();
    tempPosting = generatePostingEntity(memberEntity2, categoryEntity,
        PostingService.isNotNoticePosting, PostingService.isSecretPosting,
        PostingService.isTempPosting);
    for (Integer i = 0; i < 15; i++) {
      generatePostingEntity(memberEntity, categoryEntity,
          PostingService.isNotNoticePosting, PostingService.isNotSecretPosting,
          PostingService.isNotTempPosting);
      generatePostingEntity(memberEntity, categoryEntity,
          PostingService.isNotNoticePosting, PostingService.isNotSecretPosting,
          PostingService.isTempPosting);
    }
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("자신이 작성한 게시글 조회하기")
  public void findAllPostingById() throws Exception {
    String docMsg = "실패할 경우 알 수 없는 오류가 발생하였습니다 문구가 뜹니다.";
    String docCode = "에러가 발생할 경우: " + exceptionAdvice.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/members/posts")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(jsonPath("$.data.content.length()", lessThanOrEqualTo(10)))
        .andExpect(status().isOk())
        .andDo(document("member-show-all-post",
            requestParameters(
                parameterWithName("page").description("페이지 번호 (페이지 시작 번호 : 0)"),
                parameterWithName("size").description("한 페이지에 보이는 게시글 개수 (default : 10)")
            ),
            responseFields(
                generateResultMapPostingResponseFields(ResponseType.SINGLE, "", docCode, docMsg)
            )
        ));
  }

  @Test
  @DisplayName("자신이 임시저장한 게시글 조회하기")
  public void findAllTempPostingById() throws Exception {
    String docMsg = "실패할 경우 알 수 없는 오류가 발생하였습니다 문구가 뜹니다.";
    String docCode = "에러가 발생할 경우: " + exceptionAdvice.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/members/temp_posts")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(jsonPath("$.data.content.length()", lessThanOrEqualTo(10)))
        .andExpect(status().isOk())
        .andDo(document("member-show-all-temp-post",
            requestParameters(
                parameterWithName("page").description("페이지 번호 (페이지 시작 번호 : 0)"),
                parameterWithName("size").description("한 페이지에 보이는 게시글 개수 (default : 10)")
            ),
            responseFields(
                generateResultMapPostingResponseFields(ResponseType.SINGLE, "", docCode, docMsg)
            )
        ));
  }

  @Test
  @DisplayName("자신이 작성한 게시글 하나 조회하기")
  public void findPostingRedirect() throws Exception {
    Long postId = tempPosting.getId();
    mockMvc.perform(get("/v1/members/posts/{pid}", postId)
            .header("Authorization", userToken)
        )
        .andDo(print())
        .andExpect(redirectedUrl("/v1/post/" + postId.toString()))
        .andExpect(status().is3xxRedirection())
        .andDo(document("member-show-post",
            pathParameters(
                parameterWithName("pid").description("조회할 게시글의 아이디")
            )));
  }

  @Test
  @DisplayName("다른사람이 임시저장 글에 접근했을 때")
  public void findPostingBadAccess() throws Exception {
    // posting 조회 테스트지만 테스트용 객체들이 잘 만들어져 있어서 여기서 테스트했음
    Long postId = tempPosting.getId();

    mockMvc.perform(get("/v1/post/{pid}", postId)
            .header("Authorization", userToken)
            .param("password", "asd"))
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-11002))
        .andExpect(jsonPath("$.msg").value("임시저장 게시물입니다."))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("자신이 작성한 게시글 수정하기")
  public void updatePostingRedirect() throws Exception {
    Long postId = tempPosting.getId();
    mockMvc.perform(put("/v1/members/posts/{pid}", postId)
            .header("Authorization", userToken)
        )
        .andDo(print())
        .andExpect(redirectedUrl("/v1/post/" + postId.toString()))
        .andExpect(status().is3xxRedirection())
        .andDo(document("member-update-post",
            pathParameters(
                parameterWithName("pid").description("조회할 게시글의 아이디")
            )));
  }

  @Test
  @DisplayName("자신이 작성한 게시글 삭제하기")
  public void deletePostingRedirect() throws Exception {
    Long postId = tempPosting.getId();
    mockMvc.perform(delete("/v1/members/posts/{pid}", postId)
            .header("Authorization", userToken)
        )
        .andDo(print())
        .andExpect(redirectedUrl("/v1/post/" + postId.toString()))
        .andExpect(status().is3xxRedirection())
        .andDo(document("member-delete-post",
            pathParameters(
                parameterWithName("pid").description("조회할 게시글의 아이디")
            )));
  }

  @Test
  @DisplayName("다른 사람의 프로필에서 다른 사람이 작성한 게시글 목록 조회")
  public void findPostingListOfOtherTest() throws Exception {
    for (int i = 0; i < 10; i++) {
      generatePostingEntity(memberEntity2, categoryEntity, PostingService.isNotNoticePosting,
          PostingService.isNotSecretPosting, PostingService.isNotTempPosting);
      // TODO : temp, secret 예외 추가 필요. & 해당 예외에 대한 테스트 추가
//      generatePostingEntity(memberEntity2, categoryEntity, PostingService.isNotNoticePosting,
//          PostingService.isSecretPosting, PostingService.isTempPosting);
    }

    String docCode = "";
    String docMsg = "(나중에 예외 사항을 추가하겠습니다..!)"
        + " +\n" + "그 외 실패한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    Long otherId = memberEntity2.getId();
    mockMvc.perform(get("/v1/members/{memberId}/posts", otherId)
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "20"))
        .andDo(print())
        .andExpect(jsonPath("$.data.content.length()", greaterThan(0)))
        .andExpect(jsonPath("$.data.content.length()", lessThanOrEqualTo(10)))
        .andExpect(jsonPath("$.list.[?(@.writerId != %d)]", otherId).doesNotExist())
        .andExpect(jsonPath("$.list.[?(@.isTemp == %d)]",
            PostingService.isTempPosting).doesNotExist())
        .andExpect(jsonPath("$.list.[?(@.isSecret == %d)]",
            PostingService.isSecretPosting).doesNotExist())
        .andDo(document("member-other-posts-list",
            pathParameters(
                parameterWithName("memberId").description("조회하려는 회원 아이디")
            ),
            requestParameters(
                parameterWithName("page").description("페이지 번호 (페이지 시작 번호 : 0)"),
                parameterWithName("size").description("한 페이지에 보이는 게시글 개수 (default : 10)")
            ),
            responseFields(
                generateResultMapPostingResponseFields(ResponseType.SINGLE,
                    "성공 시: success, 실패 시: fail",
                    docCode, docMsg)
            )));
  }

  @Test
  @DisplayName("다른 사람의 프로필에서 다른 사람이 작성한 게시글 조회")
  public void findSinglePostingOfOtherTest() throws Exception {
    PostingEntity posting = generatePostingEntity(memberEntity2, categoryEntity,
        PostingService.isNotNoticePosting, PostingService.isNotSecretPosting,
        PostingService.isNotTempPosting);

    String docCode = "";
    String docMsg = "(나중에 예외 사항을 추가하겠습니다..!)"
        + " +\n" + "그 외 실패한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    Long otherId = memberEntity2.getId();
    Long postId = posting.getId();
    mockMvc.perform(get("/v1/members/{memberId}/posts/{postId}", otherId, postId)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.writerId").value(otherId))
        .andExpect(jsonPath("$.data.isTemp").value(PostingService.isNotTempPosting))
        .andExpect(jsonPath("$.data.isSecret").value(PostingService.isNotSecretPosting))
        .andDo(document("member-other-posts-single",
            pathParameters(
                parameterWithName("memberId").description("조회하려는 회원 아이디"),
                parameterWithName("postId").description("조회하려는 게시글 아이디")
            ),
            responseFields(
                generatePostingResponseFields(ResponseType.SINGLE, "성공 시: success, 실패 시: fail",
                    docCode, docMsg)
            )));
  }

  private List<FieldDescriptor> generateResultMapPostingResponseFields(ResponseType type,
      String success,
      String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".isLast").description("true: 마지막 페이지, +\nfalse: 다음 페이지 존재"),
        fieldWithPath(prefix + ".content[].id").description("게시물 ID"),
        fieldWithPath(prefix + ".content[].title").description("게시물 제목"),
        fieldWithPath(prefix + ".content[].content").description(
            "게시물 내용 (비밀 게시글일 경우 \"비밀 게시글입니다.\""),
        fieldWithPath(prefix + ".content[].writer").description("작성자 (비밀 게시글일 경우 \"익명\")"),
        fieldWithPath(prefix + ".content[].writerId").description("작성자 아이디 (비밀 게시글일 경우 -1)"),
        fieldWithPath(prefix + ".content[].writerThumbnailPath").description(
            "작성자 썸네일 이미지 조회 api path (비밀 게시글일 경우 null)").type(String.class).optional(),
        fieldWithPath(prefix + ".content[].size").description("조건에 따라 조회한 게시글의 총 개수"),
        fieldWithPath(prefix + ".content[].visitCount").description("조회 수"),
        fieldWithPath(prefix + ".content[].likeCount").description("좋아요 수"),
        fieldWithPath(prefix + ".content[].dislikeCount").description("싫어요 수"),
        fieldWithPath(prefix + ".content[].commentCount").description("댓글 수"),
        fieldWithPath(prefix + ".content[].registerTime").description("작성 시간"),
        fieldWithPath(prefix + ".content[].updateTime").description("수정 시간"),
        fieldWithPath(prefix + ".content[].ipAddress").description("IP 주소"),
        fieldWithPath(prefix + ".content[].allowComment").description("댓글 허용?"),
        fieldWithPath(prefix + ".content[].isNotice").description("공지글?"),
        fieldWithPath(prefix + ".content[].isSecret").description("비밀글?"),
        fieldWithPath(prefix + ".content[].isTemp").description("임시저장?"),
        fieldWithPath(prefix + ".content[].category").description("카테고리 이름"),
        fieldWithPath(prefix + ".content[].categoryId").description("카테고리 ID"),
        fieldWithPath(prefix + ".content[].thumbnailPath").description("게시글 썸네일 이미지 조회 api path")
            .type(String.class).optional()
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

}

