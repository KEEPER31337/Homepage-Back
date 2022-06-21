package keeper.project.homepage.user.controller.ctf;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.controller.ctf.CtfSpringTestHelper;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CtfChallengeControllerTest extends CtfSpringTestHelper {

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
  }

  @Test
  @DisplayName("문제 목록 보기 - 성공")
  public void getProblemListSuccess() throws Exception {
    CtfContestEntity contest = generateCtfContest(adminEntity, true);

    Long score = 1000L;

    CtfChallengeEntity dynamicChallenge = getCtfChallengeEntityIsSolvable(
        contest, CtfChallengeTypeEntity.DYNAMIC, CtfChallengeCategoryEntity.FORENSIC, score);
    CtfChallengeEntity standardChallenge = getCtfChallengeEntityIsSolvable(
        contest, CtfChallengeTypeEntity.STANDARD, CtfChallengeCategoryEntity.MISC, score);
    CtfChallengeEntity notSolvable = generateCtfChallenge(
        contest, CtfChallengeTypeEntity.DYNAMIC, CtfChallengeCategoryEntity.WEB, score);

    CtfTeamEntity team = generateCtfTeam(contest, userEntity, 0L);

    generateCtfFlag(team, dynamicChallenge, false);
    generateCtfFlag(team, standardChallenge, true);

    mockMvc.perform(get("/v1/ctf/prob")
            .header("Authorization", userToken)
            .param("cid", String.valueOf(contest.getId())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.list.size()").value(2))
        .andExpect(jsonPath("$.list[0].title").value(dynamicChallenge.getName()))
        .andExpect(jsonPath("$.list[0].content").doesNotExist())
        .andExpect(jsonPath("$.list[0].contestId")
            .value(dynamicChallenge.getCtfContestEntity().getId()))
        .andExpect(jsonPath("$.list[0].category.id")
            .value(dynamicChallenge.getCtfChallengeCategoryEntity().getId()))
        .andExpect(jsonPath("$.list[0].type.id").doesNotExist())
        .andExpect(jsonPath("$.list[0].isSolvable").doesNotExist())
        .andExpect(jsonPath("$.list[0].score").value(dynamicChallenge.getScore()))
        .andExpect(jsonPath("$.list[0].isSolved").value(false))
        .andExpect(jsonPath("$.list[0].dynamicInfo").doesNotExist())
        .andExpect(jsonPath("$.list[0].flag").doesNotExist())
        .andExpect(jsonPath("$.list[1].title").value(standardChallenge.getName()))
        .andExpect(jsonPath("$.list[1].content").doesNotExist())
        .andExpect(jsonPath("$.list[1].contestId")
            .value(standardChallenge.getCtfContestEntity().getId()))
        .andExpect(jsonPath("$.list[1].category.id")
            .value(standardChallenge.getCtfChallengeCategoryEntity().getId()))
        .andExpect(jsonPath("$.list[1].type.id").doesNotExist())
        .andExpect(jsonPath("$.list[1].isSolvable").doesNotExist())
        .andExpect(jsonPath("$.list[1].score").value(standardChallenge.getScore()))
        .andExpect(jsonPath("$.list[1].isSolved").value(true))
        .andExpect(jsonPath("$.list[1].dynamicInfo").doesNotExist())
        .andExpect(jsonPath("$.list[1].flag").doesNotExist())
        .andDo(document("get-common-problem-list",
            requestParameters(
                parameterWithName("cid").description("CTF 대회 id")
            ),
            responseFields(
                generateChallengeCommonDtoResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  private CtfChallengeEntity getCtfChallengeEntityIsSolvable(CtfContestEntity contest,
      CtfChallengeTypeEntity dynamic, CtfChallengeCategoryEntity forensic, Long score) {
    CtfChallengeEntity dynamicChallenge = generateCtfChallenge(contest,
        dynamic, forensic, score);
    dynamicChallenge.setIsSolvable(true);
    ctfChallengeRepository.save(dynamicChallenge);
    return dynamicChallenge;
  }

  @Test
  @DisplayName("플래그 체크 - 성공")
  public void checkFlagSuccess() throws Exception {
    CtfContestEntity contest = generateCtfContest(adminEntity, true);

    Long score = 1000L;
    Long maxScore = 1234L;
    Long minScore = 567L;

    CtfChallengeEntity dynamicChallenge = getCtfChallengeEntityIsSolvable(
        contest, CtfChallengeTypeEntity.DYNAMIC, CtfChallengeCategoryEntity.FORENSIC, score);
    generateDynamicChallengeInfo(dynamicChallenge, maxScore, minScore);

    CtfTeamEntity team = generateCtfTeam(contest, userEntity, 0L);

    CtfFlagEntity flag = generateCtfFlag(team, dynamicChallenge, false);

    String content = "{\n"
        + "    \"content\": \"" + flag.getContent() + "\"\n"
        + "}";
    mockMvc.perform(post("/v1/ctf/prob/{pid}/submit/flag", String.valueOf(dynamicChallenge.getId()))
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.content").value(flag.getContent()))
        .andExpect(jsonPath("$.data.isCorrect").value(true))
        .andDo(document("check-flag",
            pathParameters(
                parameterWithName("pid").description("문제 id")
            ),
            requestFields(
                fieldWithPath("content").description("제출한 flag 정보")
            ),
            responseFields(
                generateFlagDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n풀 수 없는 문제의 경우 실패: false", "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));

    Assertions.assertThat(team.getScore()).isEqualTo(minScore);
  }

  @Test
  @DisplayName("문제 세부 정보 보기 - 성공")
  public void getProblemDetailSuccess() throws Exception {
    CtfContestEntity contest = generateCtfContest(adminEntity, true);

    Long score = 1000L;

    CtfChallengeEntity dynamicChallenge = getCtfChallengeEntityIsSolvable(
        contest, CtfChallengeTypeEntity.DYNAMIC, CtfChallengeCategoryEntity.FORENSIC, score);
    generateFileInChallenge(dynamicChallenge);

    CtfTeamEntity team = generateCtfTeam(contest, userEntity, 0L);

    generateCtfFlag(team, dynamicChallenge, true);

    mockMvc.perform(get("/v1/ctf/prob/{pid}", String.valueOf(dynamicChallenge.getId()))
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(dynamicChallenge.getName()))
        .andExpect(jsonPath("$.data.content").value(dynamicChallenge.getDescription()))
        .andExpect(jsonPath("$.data.contestId")
            .value(dynamicChallenge.getCtfContestEntity().getId()))
        .andExpect(jsonPath("$.data.category.id")
            .value(dynamicChallenge.getCtfChallengeCategoryEntity().getId()))
        .andExpect(jsonPath("$.data.type.id").doesNotExist())
        .andExpect(jsonPath("$.data.isSolvable").doesNotExist())
        .andExpect(
            jsonPath("$.data.creatorName").value(dynamicChallenge.getCreator().getNickName()))
        .andExpect(jsonPath("$.data.score").value(dynamicChallenge.getScore()))
        .andExpect(jsonPath("$.data.solvedTeamCount").value(1L))
        .andExpect(jsonPath("$.data.isSolved").value(true))
        .andExpect(jsonPath("$.data.dynamicInfo").doesNotExist())
        .andExpect(jsonPath("$.data.flag").doesNotExist())
        .andDo(document("get-problem-detail",
            pathParameters(
                parameterWithName("pid").description("문제 id")
            ),
            responseFields(
                generateChallengeDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }
}