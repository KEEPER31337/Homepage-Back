package keeper.project.homepage.ctf.controller;

import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.MISC;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.STANDARD;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ctf.controller.CtfSpringTestHelper;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.ctf.entity.CtfTeamHasMemberEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.ctf.dto.CtfJoinTeamRequestDto;
import keeper.project.homepage.ctf.dto.CtfLeaveTeamRequestDto;
import keeper.project.homepage.ctf.dto.CtfTeamDetailDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CtfTeamControllerTest extends CtfSpringTestHelper {

  private CtfContestEntity contestEntity;

  private MemberEntity userEntity;
  private MemberEntity adminEntity;

  private String userToken;
  private String adminToken;

  @BeforeEach
  public void setUp() throws Exception {
    userEntity = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(userEntity);
    adminEntity = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(adminEntity);
    contestEntity = generateCtfContest(adminEntity, true);
  }

  @Test
  @DisplayName("팀 생성 성공")
  void createTeam() throws Exception {
    String TEAM_NAME = "test_name";
    String TEAM_DESC = "test_desc";
    CtfTeamDetailDto ctfTeamDetailDto = CtfTeamDetailDto.builder()
        .name(TEAM_NAME)
        .description(TEAM_DESC)
        .contestId(contestEntity.getId())
        .build();

    mockMvc.perform(post("/v1/ctf/team")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(ctfTeamDetailDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.name").value(TEAM_NAME))
        .andExpect(jsonPath("$.data.description").value(TEAM_DESC))
        .andExpect(jsonPath("$.data.score").value(0L))
        .andDo(document("create-team",
            requestFields(
                fieldWithPath("name").description("TEAM 이름"),
                fieldWithPath("description").description("TEAM 설명"),
                fieldWithPath("contestId").description("TEAM이 등록 될 contest Id")
            ),
            responseFields(
                generateTeamDetailDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("팀 수정 성공")
  void modifyTeam() throws Exception {
    CtfTeamEntity team = generateCtfTeam(contestEntity, userEntity, 0L);

    String MODIFIED_TEAM_NAME = "modified_test_name";
    String MODIFIED_TEAM_DESC = "modified_test_desc";
    CtfTeamDetailDto ctfTeamDetailDto = CtfTeamDetailDto.builder()
        .name(MODIFIED_TEAM_NAME)
        .description(MODIFIED_TEAM_DESC)
        .contestId(contestEntity.getId())
        .build();

    mockMvc.perform(put("/v1/ctf/team/{teamId}", team.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(ctfTeamDetailDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.name").value(MODIFIED_TEAM_NAME))
        .andExpect(jsonPath("$.data.description").value(MODIFIED_TEAM_DESC))
        .andExpect(jsonPath("$.data.score").value(0L))
        .andDo(document("modify-team",
            pathParameters(
                parameterWithName("teamId").description("수정할 TEAM의 Id")
            ),
            requestFields(
                fieldWithPath("name").description("TEAM 이름"),
                fieldWithPath("description").description("TEAM 설명"),
                fieldWithPath("contestId").description("TEAM이 등록 될 contest Id")
            ),
            responseFields(
                generateTeamDetailDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("팀 가입")
  void joinTeam() throws Exception {
    CtfTeamEntity team = generateCtfTeam(contestEntity, adminEntity, 0L);
    CtfJoinTeamRequestDto content = new CtfJoinTeamRequestDto(team.getName(),
        contestEntity.getId());

    mockMvc.perform(post("/v1/ctf/team/member")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(content)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.teamName").value(team.getName()))
        .andExpect(jsonPath("$.data.memberNickname").value(userEntity.getNickName()))
        .andDo(document("join-team",
            requestFields(
                fieldWithPath("contestId").description("Contest ID"),
                fieldWithPath("teamName").description("TEAM 이름")
            ),
            responseFields(
                generateTeamHasMemberDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("팀 탈퇴")
  void leaveTeam() throws Exception {
    CtfTeamEntity team = generateCtfTeam(contestEntity, userEntity, 0L);
    CtfLeaveTeamRequestDto content = new CtfLeaveTeamRequestDto(contestEntity.getId());

    mockMvc.perform(delete("/v1/ctf/team/member")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(content)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.name").value(team.getName()))
        .andExpect(jsonPath("$.data.description").value(team.getDescription()))
        .andDo(document("leave-team",
            requestFields(
                fieldWithPath("ctfId").description("CTF contest id")
            ),
            responseFields(
                generateTeamDetailDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("팀 세부 정보 열람")
  void getTeamDetail() throws Exception {
    CtfTeamEntity team = generateCtfTeam(contestEntity, userEntity, 0L);
    CtfTeamHasMemberEntity teamHasMemberEntity = CtfTeamHasMemberEntity.builder()
        .team(team)
        .member(adminEntity)
        .build();
    ctfTeamHasMemberRepository.save(teamHasMemberEntity);
    team.getCtfTeamHasMemberEntityList().add(teamHasMemberEntity);

    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity, STANDARD, MISC, 1234L,
        false);
    generateCtfFlag(team, challenge, true);

    mockMvc.perform(get("/v1/ctf/team/{teamId}", team.getId())
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.name").value(team.getName()))
        .andExpect(jsonPath("$.data.description").value(team.getDescription()))
        .andDo(document("get-team-detail",
            pathParameters(
                parameterWithName("teamId").description("team id")
            ),
            responseFields(
                generateTeamDetailDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("팀 목록 불러오기")
  void getTeamList() throws Exception {
    CtfTeamEntity team = generateCtfTeam(contestEntity, userEntity, 0L);
    CtfTeamEntity team2 = generateCtfTeam(contestEntity, adminEntity, 0L);

    mockMvc.perform(get("/v1/ctf/team")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10")
            .param("ctfId", String.valueOf(contestEntity.getId())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.page.content[0].name").value(team.getName()))
        .andExpect(jsonPath("$.page.content[0].description").value(team.getDescription()))
        .andExpect(jsonPath("$.page.content[1].name").value(team2.getName()))
        .andExpect(jsonPath("$.page.content[1].description").value(team2.getDescription()))
        .andDo(document("get-team-list",
            requestParameters(
                generateCommonPagingParameters("한 페이지당 출력 수(default = 10)",
                    parameterWithName("ctfId").description("CTF contest id"))
            ),
            responseFields(
                generateTeamDtoResponseFields(ResponseType.PAGE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("내가 속한 팀 세부 정보 열람")
  void getMyTeamDetail() throws Exception {
    CtfTeamEntity team = generateCtfTeam(contestEntity, userEntity, 0L);

    mockMvc.perform(get("/v1/ctf/team/{ctfId}/my-team", contestEntity.getId())
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.name").value(team.getName()))
        .andExpect(jsonPath("$.data.description").value(team.getDescription()))
        .andDo(document("get-my-team-detail",
            pathParameters(
                parameterWithName("ctfId").description("ctf id")
            ),
            responseFields(
                generateTeamDetailDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환, 가입한 팀이 없어 실패 시 -13004",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));

  }

  @Test
  @DisplayName("내가 속한 팀 세부 정보 열람 - 실패 (속한 팀 없음)")
  void getMyTeamDetailFailed() throws Exception {
//    CtfTeamEntity team = generateCtfTeam(contestEntity, userEntity, 0L);

    mockMvc.perform(get("/v1/ctf/team/{ctfId}/my-team", contestEntity.getId())
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-13004));
  }
}