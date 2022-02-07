package keeper.project.homepage.controller.member;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MemberControllerFindPostingTest extends ApiControllerTestSetUp {

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
  private PostingEntity postingEntity;

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
    JacksonJsonParser jsonParser = new JacksonJsonParser();
    userToken = jsonParser.parseMap(resultString).get("data").toString();

    categoryEntity = CategoryEntity.builder()
        .name("테스트 게시판").build();
    categoryRepository.save(categoryEntity);

    postingEntity = PostingEntity.builder()
        .title("test 게시판 제목")
        .content("test 게시판 제목 내용")
        .memberId(memberEntity2)
        .categoryId(categoryEntity)
        .ipAddress("192.11.222.333")
        .allowComment(0)
        .isNotice(0)
        .isTemp(0)
        .isSecret(1)
        .likeCount(0)
        .dislikeCount(0)
        .commentCount(0)
        .visitCount(0)
        .registerTime(new Date())
        .updateTime(new Date())
        .password("asd")
        .build();
    memberEntity.getPosting().add(postingEntity);

    postingRepository.save(postingEntity);
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
          .registerTime(new Date())
          .updateTime(new Date())
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
          .registerTime(new Date())
          .updateTime(new Date())
          .password("asd2")
          .build());
      memberEntity.getPosting().add(tempPosting);
    }
  }


  @Test
  @DisplayName("자신이 작성한 게시글 조회하기")
  public void findAllPostingById() throws Exception {
    String isExistTitle = "$.list[?(@.title == '%s')]";
    String normalTitle = "test 게시판 제목";
    mockMvc.perform(get("/v1/member/post")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "5"))
        .andDo(print())
        .andExpect(jsonPath(isExistTitle, normalTitle).exists())
        .andExpect(jsonPath(isExistTitle, normalTitle + "0").exists())
        .andExpect(jsonPath(isExistTitle, normalTitle + "1").exists())
        .andExpect(jsonPath(isExistTitle, normalTitle + "2").exists())
        .andExpect(jsonPath(isExistTitle, normalTitle + "3").exists())
        .andExpect(jsonPath(isExistTitle, normalTitle + "4").doesNotExist())
        .andExpect(status().isOk());

    mockMvc.perform(get("/v1/member/post")
            .header("Authorization", userToken)
            .param("page", "3")
            .param("size", "5"))
        .andDo(print())
        .andExpect(jsonPath(isExistTitle, normalTitle + "14").exists())
        .andExpect(jsonPath(isExistTitle, normalTitle + "15").doesNotExist())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("자신이 임시저장한 게시글 조회하기")
  public void findAllTempPostingById() throws Exception {
    String isExistTitle = "$.list[?(@.title == '%s')]";
    String tempTitle = "test 임시글 제목";
    mockMvc.perform(get("/v1/member/temp_post")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "5"))
        .andDo(print())
        .andExpect(jsonPath(isExistTitle, tempTitle + "0").exists())
        .andExpect(jsonPath(isExistTitle, tempTitle + "1").exists())
        .andExpect(jsonPath(isExistTitle, tempTitle + "2").exists())
        .andExpect(jsonPath(isExistTitle, tempTitle + "3").exists())
        .andExpect(jsonPath(isExistTitle, tempTitle + "4").exists())
        .andExpect(jsonPath(isExistTitle, tempTitle + "5").doesNotExist())
        .andExpect(status().isOk());

    mockMvc.perform(get("/v1/member/temp_post")
            .header("Authorization", userToken)
            .param("page", "3")
            .param("size", "5"))
        .andDo(print())
        .andExpect(jsonPath("$.list").isEmpty())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("자신이 작성한 게시글 하나 조회하기")
  public void findPostingRedirect() throws Exception {
    Long postId = postingEntity.getId();
    mockMvc.perform(get("/v1/member/temp_post/{pid}", postId)
            .header("Authorization", userToken)
        )
        .andDo(print())
        .andExpect(redirectedUrl("/v1/post/" + postId.toString()))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("다른사람이 임시저장 글에 접근했을 때")
  public void findPostingBadAccess() throws Exception {
    // posting 조회 테스트지만 테스트용 객체들이 잘 만들어져 있어서 여기서 테스트했음
    Long postId = postingEntity.getId();

    mockMvc.perform(get("/v1/post/{pid}", postId)
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "5"))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("자신이 작성한 게시글 수정하기")
  public void updatePostingRedirect() throws Exception {
    Long postId = postingEntity.getId();
    mockMvc.perform(put("/v1/member/post/{pid}", postId)
            .header("Authorization", userToken)
        )
        .andDo(print())
        .andExpect(redirectedUrl("/v1/post/" + postId.toString()))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("자신이 작성한 게시글 삭제하기")
  public void deletePostingRedirect() throws Exception {
    Long postId = postingEntity.getId();
    mockMvc.perform(delete("/v1/member/post/{pid}", postId)
            .header("Authorization", userToken)
        )
        .andDo(print())
        .andExpect(redirectedUrl("/v1/post/" + postId.toString()))
        .andExpect(status().is3xxRedirection());
  }
}

