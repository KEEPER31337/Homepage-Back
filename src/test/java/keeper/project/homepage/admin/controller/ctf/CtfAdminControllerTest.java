package keeper.project.homepage.admin.controller.ctf;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.admin.dto.ctf.CtfChallengeAdminDto;
import keeper.project.homepage.admin.dto.ctf.CtfContestAdminDto;
import keeper.project.homepage.admin.dto.ctf.CtfProbMakerDto;
import keeper.project.homepage.controller.ctf.CtfSpringTestHelper;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfSubmitLogEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.user.dto.ctf.CtfChallengeCategoryDto;
import keeper.project.homepage.user.dto.ctf.CtfChallengeTypeDto;
import keeper.project.homepage.user.dto.ctf.CtfDynamicChallengeInfoDto;
import keeper.project.homepage.util.service.CtfUtilService;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
class CtfAdminControllerTest extends CtfSpringTestHelper {

  private CtfContestEntity contestEntity;

  private MemberEntity userEntity;
  private MemberEntity adminEntity;
  private MemberEntity makeProbUserEntity;

  private String userToken;
  private String adminToken;
  private String makeProbUserToken;

  @BeforeEach
  public void setUp() throws Exception {
    userEntity = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????, MemberRankName.????????????);
    userToken = generateJWTToken(userEntity);
    adminEntity = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????, MemberRankName.????????????);
    adminToken = generateJWTToken(adminEntity);
    makeProbUserEntity = generateMemberEntity(MemberJobName.?????????, MemberTypeName.?????????,
        MemberRankName.????????????);
    makeProbUserToken = generateJWTToken(makeProbUserEntity);
    contestEntity = generateCtfContest(adminEntity);
  }

  @Test
  @DisplayName("?????? ???????????? CTF ?????? - ??????")
  public void createCtfContestSuccess() throws Exception {
    String CTF_NAME = "test_name";
    String CTF_DESC = "test_desc";
    CtfContestAdminDto contestDto = CtfContestAdminDto.builder()
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
                fieldWithPath("name").description("CTF ??????"),
                fieldWithPath("description").description("CTF ??????")
            ),
            responseFields(
                generateContestDtoResponseFields(ResponseType.SINGLE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("?????? ???????????? CTF ?????? - ??????")
  public void openCtfContestSuccess() throws Exception {
    Long CTF_ID = contestEntity.getId();

    mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/v1/admin/ctf/contest/{cid}/open", CTF_ID)
                .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.joinable").value(true))
        .andDo(document("open-contest",
            pathParameters(
                parameterWithName("cid").description("CTF ID")
            ),
            responseFields(
                generateContestDtoResponseFields(ResponseType.SINGLE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("?????? ???????????? CTF ????????? - ??????")
  public void closeCtfContestSuccess() throws Exception {
    Long CTF_ID = contestEntity.getId();
    contestEntity.setIsJoinable(true);
    ctfContestRepository.save(contestEntity);

    mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/v1/admin/ctf/contest/{cid}/close", CTF_ID)
                .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.joinable").value(false))
        .andDo(document("close-contest",
            pathParameters(
                parameterWithName("cid").description("CTF ID")
            ),
            responseFields(
                generateContestDtoResponseFields(ResponseType.SINGLE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("?????? ???????????? CTF ?????? ?????? - ??????")
  public void getCtfContestsSuccess() throws Exception {
    CtfContestEntity contest1 = generateCtfContest(userEntity);
    CtfContestEntity contest2 = generateCtfContest(userEntity, false);
    CtfContestEntity contest3 = generateCtfContest(userEntity, false);

    mockMvc.perform(get("/v1/admin/ctf/contests")
            .param("page", "0")
            .param("size", "10")
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.page.content[0].ctfId").value(contest3.getId()))
        .andExpect(jsonPath("$.page.content[1].ctfId").value(contest2.getId()))
        .andExpect(jsonPath("$.page.content[2].ctfId").value(contest1.getId()))
        .andDo(document("get-contests",
            requestParameters(
                generateCommonPagingParameters("??? ???????????? ?????? ???(default = 10")
            ),
            responseFields(
                generateContestDtoResponseFields(ResponseType.PAGE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("?????? ???????????? ?????? ????????? ?????? - ??????")
  public void designateProbMakerSuccess() throws Exception {
    MemberEntity probMaker = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????,
        MemberRankName.????????????);
    CtfProbMakerDto probMakerDto = CtfProbMakerDto.builder()
        .memberId(probMaker.getId())
        .build();

    mockMvc.perform(post("/v1/admin/ctf/prob/maker")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(probMakerDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.memberId").value(probMaker.getId()))
        .andDo(document("designate-probMaker",
            requestFields(
                fieldWithPath("memberId").description("???????????? ????????? member??? Id")
            ),
            responseFields(
                generateProbMakerDtoResponseFields(ResponseType.SINGLE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("?????? ???????????? ?????? ????????? ?????? - ??????")
  public void disqualifyProbMakerSuccess() throws Exception {
    // given
    MemberEntity probMaker = generateMemberEntity(MemberJobName.??????, MemberTypeName.?????????,
        MemberRankName.????????????);
    MemberJobEntity probMakerRole = memberJobRepository.findByName(
        CtfUtilService.PROBLEM_MAKER_JOB).get();
    memberHasMemberJobRepository.save(MemberHasMemberJobEntity.builder()
        .memberEntity(probMaker)
        .memberJobEntity(probMakerRole)
        .build());

    // when
    CtfProbMakerDto probMakerDto = CtfProbMakerDto.builder()
        .memberId(probMaker.getId())
        .build();

    ResultActions result = mockMvc.perform(delete("/v1/admin/ctf/prob/maker")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(probMakerDto)))
        .andDo(print());

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("disqualify-probMaker",
            requestFields(
                fieldWithPath("memberId").description("???????????? ????????? member??? Id")
            ),
            responseFields(
                generateCommonResponseFields("??????: true +\n??????: false", "?????? ??? 0??? ??????",
                    "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
    Assertions.assertThat(isDisqualifyProbMakerRole(probMaker, probMakerRole)).isTrue();
  }

  private boolean isDisqualifyProbMakerRole(MemberEntity probMaker, MemberJobEntity probMakerRole) {
    return memberHasMemberJobRepository.findAllByMemberEntity_IdAndAndMemberJobEntity_Id(
        probMaker.getId(), probMakerRole.getId()).size() == 0;
  }

  @Test
  @DisplayName("????????? ???????????? ?????? ?????? - ??????")
  public void createProblemSuccess() throws Exception {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        CtfChallengeCategoryEntity.FORENSIC);
    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(CtfChallengeTypeEntity.DYNAMIC);
    MemberEntity creator = generateMemberEntity(MemberJobName.?????????, MemberTypeName.?????????,
        MemberRankName.????????????);
    String creatorToken = generateJWTToken(creator);
    Long teamScore = 0L;
    generateCtfTeam(contestEntity, creator, teamScore);
    Long maxScore = 1000L;
    Long minScore = 100L;
    CtfDynamicChallengeInfoDto dynamicInfo = CtfDynamicChallengeInfoDto.builder()
        .maxScore(maxScore)
        .minScore(minScore)
        .build();

    String title = "test_title";
    String content = "test_content";
    Boolean isSolvable = true;
    Long score = 1234L;
    String flag = "flag{keeper}";
    CtfChallengeAdminDto challenge = CtfChallengeAdminDto.builder()
        .title(title)
        .content(content)
        .contestId(contestEntity.getId())
        .category(category)
        .type(type)
        .isSolvable(isSolvable)
        .score(score)
        .dynamicInfo(dynamicInfo)
        .flag(flag)
        .build();

    mockMvc.perform(post("/v1/admin/ctf/prob")
            .header("Authorization", creatorToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(challenge)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(title))
        .andExpect(jsonPath("$.data.content").value(content))
        .andExpect(jsonPath("$.data.contestId").value(contestEntity.getId()))
        .andExpect(jsonPath("$.data.category.id").value(category.getId()))
        .andExpect(jsonPath("$.data.type.id").value(type.getId()))
        .andExpect(jsonPath("$.data.isSolvable").value(isSolvable))
        .andExpect(jsonPath("$.data.creatorName").value(creator.getNickName()))
        .andExpect(jsonPath("$.data.score").value(dynamicInfo.getMaxScore()))
        .andExpect(jsonPath("$.data.dynamicInfo.maxScore").value(dynamicInfo.getMaxScore()))
        .andExpect(jsonPath("$.data.dynamicInfo.minScore").value(dynamicInfo.getMinScore()))
        .andExpect(jsonPath("$.data.flag").value(flag))
        .andDo(document("create-problem",
            relaxedRequestFields(
                fieldWithPath("title").description("?????? ??????"),
                fieldWithPath("content").description("?????? ??????"),
                fieldWithPath("contestId").description("????????? ?????? ??? CTF id"),
                fieldWithPath("category.id").description("?????? ???????????? Id"),
                fieldWithPath("type.id").description("?????? Type Id"),
                fieldWithPath("isSolvable").description("?????? ??? ??? ?????? ??? ??????"),
                fieldWithPath("score").description(
                    "????????? ?????? (TYPE??? DYNAMIC??? ?????? ?????? ????????? ??????????????? ?????????. ?????? ?????? maxScore??? ???????????????.)"),
                fieldWithPath("dynamicInfo.maxScore").description(
                        "TYPE??? DYNAMIC??? ?????? maxScore")
                    .optional(),
                fieldWithPath("dynamicInfo.minScore").description(
                        "TYPE??? DYNAMIC??? ?????? minScore")
                    .optional(),
                fieldWithPath("flag").description("????????? flag")
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.SINGLE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????",
                    "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("?????? ???????????? STANDARD ?????? ?????? - ??????")
  public void createStandardProblemSuccess() throws Exception {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        CtfChallengeCategoryEntity.FORENSIC);
    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(CtfChallengeTypeEntity.STANDARD);
    MemberEntity creator = generateMemberEntity(MemberJobName.?????????, MemberTypeName.?????????,
        MemberRankName.????????????);
    Long teamScore = 0L;
    generateCtfTeam(contestEntity, creator, teamScore);

    String title = "test_title";
    String content = "test_content";
    Boolean isSolvable = true;
    Long score = 1234L;
    String flag = "flag{keeper}";
    CtfChallengeAdminDto challenge = CtfChallengeAdminDto.builder()
        .title(title)
        .content(content)
        .contestId(contestEntity.getId())
        .category(category)
        .type(type)
        .isSolvable(isSolvable)
        .creatorName(creator.getNickName())
        .score(score)
        .flag(flag)
        .build();

    mockMvc.perform(post("/v1/admin/ctf/prob")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(challenge)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(title))
        .andExpect(jsonPath("$.data.content").value(content))
        .andExpect(jsonPath("$.data.contestId").value(contestEntity.getId()))
        .andExpect(jsonPath("$.data.category.id").value(category.getId()))
        .andExpect(jsonPath("$.data.type.id").value(type.getId()))
        .andExpect(jsonPath("$.data.isSolvable").value(isSolvable))
        .andExpect(jsonPath("$.data.creatorName").value(adminEntity.getNickName()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andExpect(jsonPath("$.data.flag").value(flag));
  }

  @Test
  @DisplayName("????????? ?????? ?????? - ??????")
  public void fileRegistrationInProblemSuccess() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    Long score = 1234L;
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.DYNAMIC,
        CtfChallengeCategoryEntity.FORENSIC, score);

    mockMvc.perform(multipart("/v1/admin/ctf/prob/file")
            .file(file)
            .param("challengeId", String.valueOf(challenge.getId()))
            .header("Authorization", adminToken)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.fileName").value(file.getOriginalFilename()))
        .andDo(document("file-registration",
            requestParameters(
                parameterWithName("challengeId").description("????????? ?????? id")
            ),
            requestParts(
                partWithName("file").description("?????? ????????? (form-data ?????? file= parameter ??????)")
            ),
            responseFields(
                generateFileDtoResponseFields(ResponseType.SINGLE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("?????? ?????? - ??????")
  public void openProblemSuccess() throws Exception {

    MemberEntity creator = generateMemberEntity(MemberJobName.?????????, MemberTypeName.?????????,
        MemberRankName.????????????);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.STANDARD,
        CtfChallengeCategoryEntity.MISC, score);
    generateCtfFlag(team, challenge, false);

    mockMvc.perform(patch("/v1/admin/ctf/prob/{pid}/open", challenge.getId())
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(challenge.getName()))
        .andExpect(jsonPath("$.data.content").value(challenge.getDescription()))
        .andExpect(jsonPath("$.data.contestId").value(contestEntity.getId()))
        .andExpect(jsonPath("$.data.isSolvable").value(true))
        .andExpect(jsonPath("$.data.creatorName").value(challenge.getCreator().getNickName()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andDo(document("open-problem",
            pathParameters(
                parameterWithName("pid").description("????????? ?????? id")
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.SINGLE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("?????? ?????? - ??????")
  public void closeProblemSuccess() throws Exception {

    MemberEntity creator = generateMemberEntity(MemberJobName.?????????, MemberTypeName.?????????,
        MemberRankName.????????????);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.STANDARD,
        CtfChallengeCategoryEntity.MISC, score);
    generateCtfFlag(team, challenge, false);

    mockMvc.perform(patch("/v1/admin/ctf/prob/{pid}/close", challenge.getId())
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(challenge.getName()))
        .andExpect(jsonPath("$.data.content").value(challenge.getDescription()))
        .andExpect(jsonPath("$.data.contestId").value(contestEntity.getId()))
        .andExpect(jsonPath("$.data.isSolvable").value(false))
        .andExpect(jsonPath("$.data.creatorName").value(challenge.getCreator().getNickName()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andDo(document("close-problem",
            pathParameters(
                parameterWithName("pid").description("????????? ?????? id")
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.SINGLE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("?????? ???????????? ?????? ?????? - ??????")
  public void deleteProblemSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(MemberJobName.?????????, MemberTypeName.?????????,
        MemberRankName.????????????);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.STANDARD,
        CtfChallengeCategoryEntity.MISC, score);
    generateCtfFlag(team, challenge, false);

    // when
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    mockMvc.perform(multipart("/v1/admin/ctf/prob/file")
        .file(file)
        .param("challengeId", String.valueOf(challenge.getId()))
        .header("Authorization", adminToken)
        .contentType(MediaType.MULTIPART_FORM_DATA));

    // then
    mockMvc.perform(delete("/v1/admin/ctf/prob/{pid}", challenge.getId())
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(challenge.getName()))
        .andExpect(jsonPath("$.data.content").value(challenge.getDescription()))
        .andExpect(jsonPath("$.data.contestId").value(contestEntity.getId()))
        .andExpect(jsonPath("$.data.isSolvable").value(challenge.getIsSolvable()))
        .andExpect(jsonPath("$.data.creatorName").value(challenge.getCreator().getNickName()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andDo(document("delete-problem",
            pathParameters(
                parameterWithName("pid").description("????????? ?????? id")
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.SINGLE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("????????? ???????????? ?????? ?????? ???????????? - ??????")
  public void getProblemListSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(MemberJobName.?????????, MemberTypeName.?????????,
        MemberRankName.????????????);
    String probMakerToken = generateJWTToken(creator);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.STANDARD,
        CtfChallengeCategoryEntity.MISC, score);
    CtfChallengeEntity challenge2 = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.DYNAMIC,
        CtfChallengeCategoryEntity.FORENSIC, score);
    generateDynamicChallengeInfo(challenge2, 1000L, 100L);
    generateCtfFlag(team, challenge2, false);
    generateCtfFlag(team, challenge, false);

    // when
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    mockMvc.perform(multipart("/v1/admin/ctf/prob/file")
        .file(file)
        .param("challengeId", String.valueOf(challenge.getId()))
        .param("page", "0")
        .param("size", "10")
        .header("Authorization", adminToken)
        .contentType(MediaType.MULTIPART_FORM_DATA));

    // then
    mockMvc.perform(get("/v1/admin/ctf/prob")
            .header("Authorization", probMakerToken)
            .param("ctfId", String.valueOf(contestEntity.getId())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("get-problem-list",
            requestParameters(
                generateCommonPagingParameters("??? ???????????? ?????? ???(default = 10)",
                    parameterWithName("ctfId").description("?????? ????????? ??? CTF id"))
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.PAGE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }

  @Test
  @DisplayName("????????? ???????????? ?????? ?????? ???????????? - ??????")
  public void getSubmitLogListSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(MemberJobName.?????????, MemberTypeName.?????????,
        MemberRankName.????????????);
    String probMakerToken = generateJWTToken(creator);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.STANDARD,
        CtfChallengeCategoryEntity.MISC, score);
    CtfChallengeEntity challenge2 = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.DYNAMIC,
        CtfChallengeCategoryEntity.FORENSIC, score);
    generateDynamicChallengeInfo(challenge2, 1000L, 100L);
    generateCtfFlag(team, challenge2, false);
    generateCtfFlag(team, challenge, false);

    CtfSubmitLogEntity log1 = generateCtfSubmitLog(team, creator, challenge, "??? ?????????");
    CtfSubmitLogEntity log2 = generateCtfSubmitLog(team, adminEntity, challenge, "????????? ??????");
    CtfSubmitLogEntity log3 = generateCtfSubmitLog(team, creator, challenge2, "KEEPER ??????");
    CtfSubmitLogEntity log4 = generateCtfSubmitLog(team, adminEntity, challenge2, "?????? ??????????");

    // then
    mockMvc.perform(get("/v1/admin/ctf/submit-log/{cid}", contestEntity.getId())
            .header("Authorization", probMakerToken)
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.page.content[0].id").value(log4.getId()))
        .andExpect(jsonPath("$.page.content[1].id").value(log3.getId()))
        .andExpect(jsonPath("$.page.content[2].id").value(log2.getId()))
        .andExpect(jsonPath("$.page.content[3].id").value(log1.getId()))
        .andDo(document("get-submitLog-list",
            pathParameters(
                parameterWithName("cid").description("contest Id")
            ),
            requestParameters(
                generateCommonPagingParameters("??? ???????????? ?????? ???(default = 10)")
            ),
            responseFields(
                generateCtfSubmitLogDtoResponseFields(ResponseType.PAGE,
                    "??????: true +\n??????: false", "?????? ??? 0??? ??????", "??????: ????????????????????? +\n??????: ?????? ????????? ??????")
            )));
  }
}