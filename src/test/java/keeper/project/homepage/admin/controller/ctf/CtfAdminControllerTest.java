package keeper.project.homepage.admin.controller.ctf;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.admin.dto.ctf.CtfContestDto;
import keeper.project.homepage.controller.ctf.CtfControllerTestHelper;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
class CtfAdminControllerTest extends CtfControllerTestHelper {

  private CtfContestEntity contestEntity;

  private MemberEntity userEntity;
  private MemberEntity adminEntity;
  private MemberEntity makeProbUserEntity;

  private String userToken;
  private String adminToken;
  private String makeProbUserToken;

  @BeforeEach
  public void setUp() throws Exception {
    userEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(userEntity);
    adminEntity = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(adminEntity);
    makeProbUserEntity = generateMemberEntity(MemberJobName.출제자, MemberTypeName.정회원,
        MemberRankName.일반회원);
    makeProbUserToken = generateJWTToken(makeProbUserEntity);
    contestEntity = generateCtfContest(adminEntity);
  }

  @Test
  @DisplayName("회장 권한으로 CTF 주최 - 성공")
  public void createCtfContestSuccess() throws Exception {
    String CTF_NAME = "test_name";
    String CTF_DESC = "test_desc";
    CtfContestDto contestDto = CtfContestDto.builder()
        .name(CTF_NAME)
        .description(CTF_DESC)
        .build();

    mockMvc.perform(post("/v1/admin/ctf/contest")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(contestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.name").value(CTF_NAME))
        .andExpect(jsonPath("$.data.description").value(CTF_DESC))
        .andExpect(jsonPath("$.data.joinable").value(false))
        .andDo(document("create-contest",
            requestFields(
                fieldWithPath("name").description("CTF 이름"),
                fieldWithPath("description").description("CTF 설명")
            ),
            responseFields(
                generateContestDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("회장 권한으로 CTF 개최 - 성공")
  public void openCtfContestSuccess() throws Exception {
    Long CTF_ID = contestEntity.getId();

    mockMvc.perform(patch("/v1/admin/ctf/contest/open")
            .header("Authorization", adminToken)
            .param("ctfId", String.valueOf(CTF_ID)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.joinable").value(true))
        .andDo(document("open-contest",
            requestParameters(
                parameterWithName("ctfId").description("CTF ID")
            ),
            responseFields(
                generateContestDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("회장 권한으로 CTF 끝내기 - 성공")
  public void closeCtfContestSuccess() throws Exception {
    Long CTF_ID = contestEntity.getId();
    contestEntity.setIsJoinable(true);
    ctfContestRepository.save(contestEntity);

    mockMvc.perform(patch("/v1/admin/ctf/contest/close")
            .header("Authorization", adminToken)
            .param("ctfId", String.valueOf(CTF_ID)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.joinable").value(false))
        .andDo(document("close-contest",
            requestParameters(
                parameterWithName("ctfId").description("CTF ID")
            ),
            responseFields(
                generateContestDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("회장 권한으로 CTF 목록 조회 - 성공")
  public void getCtfContestsSuccess() throws Exception {
    mockMvc.perform(get("/v1/admin/ctf/contests")
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("get-contests",
            responseFields(
                generateContestDtoResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }
}