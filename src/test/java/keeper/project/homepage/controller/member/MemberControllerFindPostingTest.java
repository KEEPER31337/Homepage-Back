package keeper.project.homepage.controller.member;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.common.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.common.entity.posting.CategoryEntity;
import keeper.project.homepage.common.entity.posting.PostingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MemberControllerFindPostingTest extends MemberControllerTestSetup {

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
        .generation(getMemberGeneration())
        .build();
    memberRepository.save(memberEntity);
    memberEntity2 = MemberEntity.builder()
        .loginId(loginId + "2")
        .password(passwordEncoder.encode(password))
        .realName(realName + "2")
        .nickName(nickName + "2")
        .emailAddress(emailAddress + "2")
        .studentId(studentId + "2")
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .generation(getMemberGeneration())
        .build();
    memberRepository.save(memberEntity2);

    String content = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
        + "    \"password\": \"" + password + "\"\n"
        + "}";
    MvcResult result = mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    String resultString = result.getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    SingleResult<SignInDto> sign = mapper.readValue(resultString, new TypeReference<>() {
    });
    userToken = sign.getData().getToken();

    categoryEntity = CategoryEntity.builder()
        .name("테스트 게시판").build();
    categoryRepository.save(categoryEntity);

    tempPosting = PostingEntity.builder()
        .title("test 임시글 제목")
        .content("test 임시글 내용")
        .memberId(memberEntity2)
        .categoryId(categoryEntity)
        .ipAddress("192.11.222.333")
        .allowComment(0)
        .isNotice(0)
        .isTemp(1)
        .isSecret(1)
        .likeCount(0)
        .dislikeCount(0)
        .commentCount(0)
        .visitCount(0)
        .registerTime(LocalDateTime.now())
        .updateTime(LocalDateTime.now())
        .password("asd")
        .build();
    memberEntity2.getPosting().add(tempPosting);

    postingRepository.save(tempPosting);
    for (Integer i = 0; i < 15; i++) {
      PostingEntity posting = postingRepository.save(PostingEntity.builder()
          .title("test 게시판 제목" + i.toString())
          .content("test 게시판 제목 내용" + i.toString())
          .memberId(memberEntity)
          .categoryId(categoryEntity)
          .ipAddress("192.11.223")
          .allowComment(0)
          .isNotice(0)
          .isSecret(0)
          .isTemp(0)
          .likeCount(0)
          .dislikeCount(1)
          .commentCount(0)
          .visitCount(0)
          .registerTime(LocalDateTime.now())
          .updateTime(LocalDateTime.now())
          .password("asd2")
          .build());
      memberEntity.getPosting().add(posting);
    }
    for (Integer i = 0; i < 15; i++) {
      PostingEntity tempPosting = postingRepository.save(PostingEntity.builder()
          .title("test 임시글 제목" + i.toString())
          .content("test 임시글 내용" + i.toString())
          .memberId(memberEntity)
          .categoryId(categoryEntity)
          .ipAddress("192.11.223")
          .allowComment(0)
          .isNotice(0)
          .isSecret(0)
          .isTemp(1)
          .likeCount(0)
          .dislikeCount(1)
          .commentCount(0)
          .visitCount(0)
          .registerTime(LocalDateTime.now())
          .updateTime(LocalDateTime.now())
          .password("asd2")
          .build());
      memberEntity.getPosting().add(tempPosting);
    }
  }


  @Test
  @DisplayName("자신이 작성한 게시글 조회하기")
  public void findAllPostingById() throws Exception {
    String docMsg = "실패할 경우 알 수 없는 오류가 발생하였습니다 문구가 뜹니다.";
    String docCode = "에러가 발생할 경우: " + exceptionAdvice.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/member/post")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(jsonPath("$.list.length()", lessThanOrEqualTo(10)))
        .andExpect(status().isOk())
        .andDo(document("member-show-all-post",
            requestParameters(
                parameterWithName("page").description("페이지 번호 (페이지 시작 번호 : 0)"),
                parameterWithName("size").description("한 페이지에 보이는 게시글 개수 (default : 10)")
            ),
            responseFields(
                generatePostingResponseFields(ResponseType.LIST, "", docCode, docMsg)
            )
        ));
  }

  //  FIXME 정채원 고태영
  @Test
  @DisplayName("자신이 임시저장한 게시글 조회하기")
  public void findAllTempPostingById() throws Exception {
    String docMsg = "실패할 경우 알 수 없는 오류가 발생하였습니다 문구가 뜹니다.";
    String docCode = "에러가 발생할 경우: " + exceptionAdvice.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/member/temp_post")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(jsonPath("$.list.length()", lessThanOrEqualTo(10)))
        .andExpect(status().isOk())
        .andDo(document("member-show-all-temp-post",
            requestParameters(
                parameterWithName("page").description("페이지 번호 (페이지 시작 번호 : 0)"),
                parameterWithName("size").description("한 페이지에 보이는 게시글 개수 (default : 10)")
            ),
            responseFields(
                generatePostingResponseFields(ResponseType.LIST, "", docCode, docMsg)
            )
        ));
  }

  @Test
  @DisplayName("자신이 작성한 게시글 하나 조회하기")
  public void findPostingRedirect() throws Exception {
    Long postId = tempPosting.getId();
    mockMvc.perform(get("/v1/member/post/{pid}", postId)
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
        .andExpect(jsonPath("$.code").value(-1))
        .andExpect(jsonPath("$.msg").value("임시저장 게시물입니다."))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("자신이 작성한 게시글 수정하기")
  public void updatePostingRedirect() throws Exception {
    Long postId = tempPosting.getId();
    mockMvc.perform(put("/v1/member/post/{pid}", postId)
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
    mockMvc.perform(delete("/v1/member/post/{pid}", postId)
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
}

