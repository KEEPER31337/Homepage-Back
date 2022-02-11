package keeper.project.homepage.controller.etc;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class AboutTitleControllerTest extends ApiControllerTestSetUp {

  private String adminToken;

  final private String ipAddress1 = "127.0.0.1";

  final private String adminLoginId = "hyeonmoAdmin";
  final private String adminPassword = "keeper2";
  final private String adminRealName = "JeongHyeonMo2";
  final private String adminNickName = "JeongHyeonMo2";
  final private String adminEmailAddress = "test2@k33p3r.com";
  final private String adminStudentId = "201724580";
  final private String adminPhoneNumber = "0100100101";
  final private int adminPoint = 50;

  private ThumbnailEntity thumbnailEntity;
  private FileEntity imageEntity;

  @BeforeEach
  public void setUp() throws Exception {
    imageEntity = FileEntity.builder()
        .fileName("bef.jpg")
        .filePath("keeper_files" + File.separator + "bef.jpg")
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(imageEntity);

    thumbnailEntity = ThumbnailEntity.builder()
        .path("keeper_files" + File.separator + "thumbnail" + File.separator + "thumb_bef.jpg")
        .file(imageEntity).build();
    thumbnailRepository.save(thumbnailEntity);

    StaticWriteTitleEntity staticWriteTitleEntity = StaticWriteTitleEntity.builder()
        .title("동아리 지원")
        .type("activity")
        .build();

    staticWriteTitleRepository.save(staticWriteTitleEntity);

    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = StaticWriteSubtitleImageEntity.builder()
        .subtitle("세미나")
        .staticWriteTitle(staticWriteTitleEntity)
        .thumbnail(thumbnailEntity)
        .displayOrder(0)
        .build();

    staticWriteSubtitleImageRepository.save(staticWriteSubtitleImageEntity);

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
        .memberType(memberTypeEntity)
        .memberRank(memberRankEntity)
        .thumbnail(thumbnailEntity)
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
  @DisplayName("페이지 블럭 리스트 불러오기 - 성공(타입을 통해)")
  public void getAllPageBlockSuccessByType() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/about/title/type/{type}", "intro"));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("페이지 블럭 리스트 불러오기 - 실패(존재하지 않는 타입)")
  public void getAllPageBlockFailByType() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/about/title/type/{type}", "intro1"));

    result.andExpect(MockMvcResultMatchers.status().is5xxServerError())
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 타입입니다."));
  }

  @Test
  @DisplayName("페이지 블럭 불러오기 - 성공(타이틀을 통해)")
  public void getPageBlockSuccessByTitle() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/about/title/{title}", "동아리 지원"));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("페이지 블럭 불러오기 - 실패(존재하지 않는 타이틀)")
  public void getPageBlockFailByTitle() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/about/title/{title}", "동아리 지원1"));

    result.andExpect(MockMvcResultMatchers.status().is5xxServerError())
        .andDo(print())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 제목입니다."));

  }

  @Test
  @DisplayName("페이지 블럭 타이틀 생성하기")
  public void createTitle() throws Exception {
    String content = "{\n"
        + "\"title\":\"" + "keeper 소개" + "\",\n"
        + "\"type\":\"" + "intro" + "\""
        + "}";

    mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/about/title/new")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 수정하기 - 성공")
  public void modifyTitleSuccessById() throws Exception {
    String content = "{\n"
        + "\"title\":\"" + "변경된 타이틀" + "\",\n"
        + "\"type\":\"" + "intro" + "\""
        + "}";

    mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/about/title/modify/{id}", 1)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 수정하기 - 실패(존재하지 않는 ID)")
  public void modifyTitleFailById() throws Exception {
    String content = "{\n"
        + "\"title\":\"" + "변경된 타이틀" + "\",\n"
        + "\"type\":\"" + "intro" + "\""
        + "}";

    mockMvc.perform(RestDocumentationRequestBuilders.put("/v1/about/title/modify/{id}", 100)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 ID입니다."));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 삭제하기 - 성공")
  public void deleteTitleSuccessById() throws Exception {
    mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/about/title/delete/{id}", 1)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 삭제하기 - 실패(권한 부족)")
  public void deleteTitleFailById() throws Exception {
    mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/about/title/delete/{id}", 1)
            .header("Authorization", "XXXX"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("보유한 권한으로 접근할수 없는 리소스 입니다"));
  }
}
