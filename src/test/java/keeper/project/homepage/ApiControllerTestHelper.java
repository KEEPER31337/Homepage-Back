package keeper.project.homepage;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;

public class ApiControllerTestHelper extends ApiControllerTestSetUp {

  final public String memberPassword = "memberPassword";
  final public String postingPassword = "postingPassword";

  public enum MemberJobName {
    회장("ROLE_회장"),
    부회장("ROLE_부회장"),
    대외부장("ROLE_대외부장"),
    학술부장("ROLE_학술부장"),
    전산관리자("ROLE_전산관리자"),
    서기("ROLE_서기"),
    총무("ROLE_총무"),
    사서("ROLE_사서"),
    회원("ROLE_회원");

    private String jobName;

    MemberJobName(String jobName) {
      this.jobName = jobName;
    }

    public String getJobName() {
      return jobName;
    }
  }

  public enum ResponseType {
    SINGLE("data"),
    LIST("list[]");

    private final String reponseFieldPrefix;

    ResponseType(String reponseFieldPrefix) {
      this.reponseFieldPrefix = reponseFieldPrefix;
    }

    public String getReponseFieldPrefix() {
      return reponseFieldPrefix;
    }
  }

  public MemberEntity generateMemberEntity(MemberJobName jobName) {
    final String epochTime = Long.toHexString(System.nanoTime());
    MemberJobEntity memberJobEntity = memberJobRepository.findByName(jobName.getJobName()).get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberJobEntity)
        .build();
    return memberRepository.saveAndFlush(MemberEntity.builder()
        .loginId("LoginId" + epochTime)
        .password(passwordEncoder.encode(memberPassword))
        .realName("RealName")
        .nickName("NickName")
        .emailAddress("member" + epochTime + "@k33p3r.com")
        .studentId(epochTime)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build());
  }

  public String generateJWTToken(String loginId, String password) throws Exception {
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
    return sign.getData().getToken();
  }

  public CategoryEntity generateCategoryEntity() {
    final String epochTime = Long.toHexString(System.nanoTime());
    return categoryRepository.save(
        CategoryEntity.builder().name("testCategory" + epochTime).build());
  }

  public PostingEntity generatePostingEntity(MemberEntity writer, CategoryEntity category,
      Integer isNotice, Integer isSecret, Integer isTemp) {
    final String epochTime = Long.toHexString(System.nanoTime());
    final LocalDateTime now = LocalDateTime.now();
    return postingRepository.save(PostingEntity.builder()
        .title("posting 제목 " + epochTime)
        .content("posting 내용 " + epochTime)
        .categoryId(category)
        .ipAddress("192.111.222.333")
        .allowComment(0)
        .isNotice(isNotice)
        .isSecret(isSecret)
        .isTemp(isTemp)
        .likeCount(0)
        .dislikeCount(0)
        .commentCount(0)
        .visitCount(0)
        .registerTime(now)
        .updateTime(now)
        .password(postingPassword)
        .memberId(writer)
        .build());
  }

  public CommentEntity generateCommentEntity(PostingEntity posting, MemberEntity writer,
      Long parentId) {
    final String epochTime = Long.toHexString(System.nanoTime());
    final LocalDateTime now = LocalDateTime.now();
    final String content = (parentId == 0L ? "댓글 내용 " : parentId + "의 대댓글 내용 ") + epochTime;
    return commentRepository.save(CommentEntity.builder()
        .content(content)
        .registerTime(now)
        .updateTime(now)
        .ipAddress("111.111.111.111")
        .likeCount(0)
        .dislikeCount(0)
        .parentId(parentId)
        .member(writer)
        .postingId(posting)
        .build());
  }
  
  public List<FieldDescriptor> generateCommonResponseFields(String docSuccess, String docCode,
      String docMsg) {
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(Arrays.asList(
        fieldWithPath("success").description(docSuccess),
        fieldWithPath("code").description(docCode),
        fieldWithPath("msg").description(docMsg)));
    return commonFields;
  }
}
