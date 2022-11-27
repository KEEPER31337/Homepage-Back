package keeper.project.homepage.ctf.controller;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CtfRankingControllerTest extends CtfSpringTestHelper {

  @Test
  @DisplayName("스코어보드 랭킹 불러오기 - 성공")
  void getRankingList_sameScore_orderBySolvedTimeDesc() throws Exception {
    MemberEntity creator = generateMemberEntity(회원, 정회원, 일반회원);
    String userToken = generateJWTToken(creator);
    CtfContestEntity contest = generateCtfContest(creator);

    // 상위 4개 팀
    generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 1000L);
    generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 1000L);
    generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 1000L);
    generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 1000L);

    MemberEntity member1 = generateMemberEntity(회원, 정회원, 일반회원);
    generateCtfTeam(contest, member1, 100L);

    MemberEntity member2 = generateMemberEntity(회원, 정회원, 일반회원);
    generateCtfTeam(contest, member2, 400L);

    MemberEntity member3 = generateMemberEntity(회원, 정회원, 일반회원);
    generateCtfTeam(contest, member3, 200L);

    MemberEntity member4 = generateMemberEntity(회원, 정회원, 일반회원);
    generateCtfTeam(contest, member4, 300L);

    mockMvc.perform(get("/v1/ctf/ranking")
            .header("Authorization", userToken)
            .param("page", "1")
            .param("size", "4")
            .param("ctfId", String.valueOf(contest.getId())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.page.content.size()").value(4))
        .andExpect(jsonPath("$.page.content[0].score").value(400L))
        .andExpect(jsonPath("$.page.content[1].score").value(300L))
        .andExpect(jsonPath("$.page.content[2].score").value(200L))
        .andExpect(jsonPath("$.page.content[3].score").value(100L))
        .andExpect(jsonPath("$.page.content[0].rank").value(5L))
        .andExpect(jsonPath("$.page.content[1].rank").value(6L))
        .andExpect(jsonPath("$.page.content[2].rank").value(7L))
        .andExpect(jsonPath("$.page.content[3].rank").value(8L))
        .andDo(document("get-ranking-list",
            requestParameters(
                generateCommonPagingParameters("한 페이지당 출력 수(default = 10)",
                    parameterWithName("ctfId").description("팀 목록을 볼 CTF id"))
            ),
            responseFields(
                generateRankingDtoResponseFields(ResponseType.PAGE,
                    "성공: true +\n실패: false", "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("스코어보드 랭킹 불러오기 - 동점자의 경우 문제 푼 시간이 빠른 순으로")
  void getRankingList() throws Exception {
    MemberEntity creator = generateMemberEntity(회원, 정회원, 일반회원);
    String userToken = generateJWTToken(creator);
    CtfContestEntity contest = generateCtfContest(creator);

    CtfTeamEntity team1 = generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 100L);
    CtfTeamEntity team2 = generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 100L);
    CtfTeamEntity team3 = generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 100L);
    CtfTeamEntity team4 = generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 100L);
    CtfTeamEntity team5 = generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 100L);
    CtfTeamEntity team6 = generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 100L);
    CtfTeamEntity team7 = generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 100L);
    CtfTeamEntity team8 = generateCtfTeam(contest, generateMemberEntity(회원, 정회원, 일반회원), 100L);

    mockMvc.perform(get("/v1/ctf/ranking")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "8")
            .param("ctfId", String.valueOf(contest.getId())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.page.content.size()").value(8))
        .andExpect(jsonPath("$.page.content[0].rank").value(1L))
        .andExpect(jsonPath("$.page.content[1].rank").value(2L))
        .andExpect(jsonPath("$.page.content[2].rank").value(3L))
        .andExpect(jsonPath("$.page.content[3].rank").value(4L))
        .andExpect(jsonPath("$.page.content[4].rank").value(5L))
        .andExpect(jsonPath("$.page.content[5].rank").value(6L))
        .andExpect(jsonPath("$.page.content[6].rank").value(7L))
        .andExpect(jsonPath("$.page.content[7].rank").value(8L))
        .andExpect(jsonPath("$.page.content[0].id").value(team1.getId()))
        .andExpect(jsonPath("$.page.content[1].id").value(team2.getId()))
        .andExpect(jsonPath("$.page.content[2].id").value(team3.getId()))
        .andExpect(jsonPath("$.page.content[3].id").value(team4.getId()))
        .andExpect(jsonPath("$.page.content[4].id").value(team5.getId()))
        .andExpect(jsonPath("$.page.content[5].id").value(team6.getId()))
        .andExpect(jsonPath("$.page.content[6].id").value(team7.getId()))
        .andExpect(jsonPath("$.page.content[7].id").value(team8.getId()));
  }
}