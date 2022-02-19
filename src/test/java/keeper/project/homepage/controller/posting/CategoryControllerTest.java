package keeper.project.homepage.controller.posting;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class CategoryControllerTest extends ApiControllerTestSetUp {

  private String adminToken;

  final private String rootName = "root_1";
  final private Long parentId = 0L;

  final private String childName = "child";

  final private String adminLoginId = "hyeonmoAdmin";
  final private String adminPassword = "keeper2";
  final private String adminRealName = "JeongHyeonMo2";
  final private String adminNickName = "JeongHyeonMo2";
  final private String adminEmailAddress = "test2@k33p3r.com";
  final private String adminStudentId = "201724580";
  final private String adminPhoneNumber = "0100100101";
  final private int adminPoint = 50;

  private CategoryEntity rootEntity;
  private CategoryEntity childEntity;

  @BeforeEach
  public void setUp() throws Exception {
    rootEntity = CategoryEntity.builder()
        .name(rootName)
        .parentId(parentId)
        .build();
    categoryRepository.save(rootEntity);

    childEntity = CategoryEntity.builder()
        .name(childName)
        .parentId(rootEntity.getId())
        .build();
    categoryRepository.save(childEntity);

    MemberJobEntity memberAdminJobEntity = memberJobRepository.findByName("ROLE_회장").get();
    MemberTypeEntity memberTypeEntity = memberTypeRepository.findByName("정회원").get();
    MemberRankEntity memberRankEntity = memberRankRepository.findByName("일반회원").get();
    MemberHasMemberJobEntity hasMemberAdminJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberAdminJobEntity)
        .build();
    MemberEntity memberAdmin = MemberEntity.builder()
        .loginId(adminLoginId)
        .password(passwordEncoder.encode(adminPassword))
        .realName(adminRealName)
        .nickName(adminNickName)
        .emailAddress(adminEmailAddress)
        .studentId(adminStudentId)
        .point(adminPoint)
        .generation(0F)
        .memberType(memberTypeEntity)
        .memberRank(memberRankEntity)
        .memberJobs(new ArrayList<>(List.of(hasMemberAdminJobEntity)))
        .build();
    memberRepository.save(memberAdmin);

    String adminContent = "{\n"
        + "    \"loginId\": \"" + adminLoginId + "\",\n"
        + "    \"password\": \"" + adminPassword + "\"\n"
        + "}";
    MvcResult adminResult = mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(adminContent))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    ObjectMapper mapper = new ObjectMapper();
    String adminResultString = adminResult.getResponse().getContentAsString();
    SingleResult<SignInDto> adminSign = mapper.readValue(adminResultString, new TypeReference<>() {
    });
    adminToken = adminSign.getData().getToken();
  }

  @Test
  @DisplayName("부모 카테고리 ID로 카테고리 불러오기")
  public void getAllCategoryByParentId() throws Exception {
    CategoryEntity findEntity = categoryRepository.findByName(rootName);

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/category/id/{parentId}", 0L));

    result.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("categories-getByParentId",
            pathParameters(
                parameterWithName("parentId").description("찾고자 하는 카테고리의 ID(최상위 카테고리 = 0)")
            ),
            responseFields(
                fieldWithPath("[].id").description("카테고리 ID"),
                fieldWithPath("[].name").description("카테고리 이름"),
                subsectionWithPath("[].children[]").description("하위 카테고리 리스트")
            )
        ));
  }

  @Test
  @DisplayName("카테고리 이름으로 카테고리 불러오기")
  public void getAllCategoryByName() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/category/name/{name}", "child"));

    result.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("categories-getByName",
            pathParameters(
                parameterWithName("name").description("찾고자 하는 카테고리의 이름")
            ),
            responseFields(
                fieldWithPath("[].id").description("카테고리 ID"),
                fieldWithPath("[].name").description("카테고리 이름"),
                subsectionWithPath("[].children[]").description("하위 카테고리 리스트")
            )
        ));
  }

  @Test
  @DisplayName("카테고리 생성 - 성공")
  public void createCategorySuccess() throws Exception {
    String content = "{\n"
        + "\"name\":\"" + "이전 스터디" + "\",\n"
        + "\"parentId\":\"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/category/new")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-create",
            requestFields(
                fieldWithPath("name").description("생성하고자 하는 카테고리 이름"),
                fieldWithPath("parentId").description("생성하는 카테고리의 상위 카테고리 지정(존재하지 않으면 0으로 지정)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("생성된 카테고리 ID"),
                fieldWithPath("data.name").description("생성된 카테고리 이름"),
                subsectionWithPath("data.children[]").description("하위 카테고리 리스트")
            )));
  }

  @Test
  @DisplayName("카테고리 생성 - 성공(null parent)")
  public void createCategoryNullSuccess() throws Exception {
    String content = "{\n"
        + "\"name\":\"" + "이전 스터디" + "\",\n"
        + "\"parentId\":\"" + null + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/category/new")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("카테고리 생성 - 성공(root parent)")
  public void createCategoryRootSuccess() throws Exception {
    String content = "{\n"
        + "\"name\":\"" + "이전 스터디" + "\",\n"
        + "\"parentId\":\"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/category/new")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("카테고리 생성 - 실패(존재하지 않는 parent)")
  public void createCategoryFail() throws Exception {
    String content = "{\n"
        + "\"name\":\"" + "이전 스터디" + "\",\n"
        + "\"parentId\":\"" + 1234 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/category/new")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("해당 부모 카테고리가 존재하지 않습니다."));
  }

  @Test
  @DisplayName("카테고리 수정 - 성공")
  public void modifyCategorySuccess() throws Exception {
    String content = "{\n"
        + "\"name\":\"" + "수정된 카테고리 이름" + "\",\n"
        + "\"parentId\":\"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/category/modify/{id}", childEntity.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-modify",
            pathParameters(
                parameterWithName("id").description("수정을 원하는 카테고리의 ID")
            ),
            requestFields(
                fieldWithPath("name").description("수정을 원하는 카테고리 이름(변하지 않는 경우 기존의 값 전달)"),
                fieldWithPath("parentId").description(
                    "수정을 원하는 상위 카테고리 ID(존재하지 않으면 0, 수정을 원하지 않으면 기존의 값 전달)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("수정된 카테고리 ID"),
                fieldWithPath("data.name").description("수정된 카테고리 이름"),
                subsectionWithPath("data.children[]").description("하위 카테고리 리스트")
            )));
  }

  @Test
  @DisplayName("카테고리 수정 - 실패(존재하지 않는 카테고리)")
  public void modifyCategoryFailByNullCategory() throws Exception {
    String content = "{\n"
        + "\"name\":\"" + "수정된 카테고리 이름" + "\",\n"
        + "\"parentId\":\"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/category/modify/{id}", 1234)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("해당 카테고리가 존재하지 않습니다."));
  }

  @Test
  @DisplayName("카테고리 수정 - 실패(존재하지 않는 부모 카테고리)")
  public void modifyCategoryFailByNullParentCategory() throws Exception {
    String content = "{\n"
        + "\"name\":\"" + "수정된 카테고리 이름" + "\",\n"
        + "\"parentId\":\"" + 1234 + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/category/modify/{id}", childEntity.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("해당 부모 카테고리가 존재하지 않습니다."));
  }

  @Test
  @DisplayName("카테고리 수정 - 실패(접근할 수 없는 root 카테고리)")
  public void modifyCategoryFailByRootCategory() throws Exception {
    String content = "{\n"
        + "\"name\":\"" + "수정된 카테고리 이름" + "\",\n"
        + "\"parentId\":\"" + 1234 + "\"\n"
        + "}";

    mockMvc.perform(put("/v1/category/modify/{id}", 0)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("루트 카테고리에 접근하였습니다."));
  }

  @Test
  @DisplayName("카테고리 삭제- 성공")
  public void deleteCategorySuccessById() throws Exception {
    mockMvc.perform(delete("/v1/category/delete/{id}", childEntity.getId())
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-delete",
            pathParameters(
                parameterWithName("id").description("삭제를 원하는 카테고리의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("삭제된 카테고리 ID"),
                fieldWithPath("data.name").description("삭제된 카테고리 이름"),
                subsectionWithPath("data.children[]").description("하위 카테고리 리스트")
            )));

  }

  @Test
  @DisplayName("카테고리 삭제- 실패(존재하지 않는 카테고리 ID)")
  public void deleteCategoryFailById() throws Exception {
    mockMvc.perform(delete("/v1/category/delete/{id}", 1234)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("해당 카테고리가 존재하지 않습니다."));

  }

  @Test
  @DisplayName("카테고리 삭제- 실패(접근 할 수 없는 root 카테고리 접근)")
  public void deleteCategoryFailRootById() throws Exception {
    mockMvc.perform(delete("/v1/category/delete/{id}", 0)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("루트 카테고리에 접근하였습니다."));

  }
}
