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
import keeper.project.homepage.admin.dto.ctf.CtfContestDto;
import keeper.project.homepage.admin.dto.ctf.CtfProbMakerDto;
import keeper.project.homepage.controller.ctf.CtfControllerTestHelper;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.ctf.CtfChallengeCategoryDto;
import keeper.project.homepage.user.dto.ctf.CtfChallengeTypeDto;
import keeper.project.homepage.user.dto.ctf.CtfDynamicChallengeInfoDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
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
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("회장 권한으로 CTF 끝내기 - 성공")
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

  @Test
  @DisplayName("회장 권한으로 문제 출제자 지정 - 성공")
  public void designateProbMakerSuccess() throws Exception {
    MemberEntity probMaker = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원,
        MemberRankName.일반회원);
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
                fieldWithPath("memberId").description("출제자로 지정할 member의 Id")
            ),
            responseFields(
                generateProbMakerDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("출제자 권한으로 문제 생성 - 성공")
  public void createProblemSuccess() throws Exception {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        CtfChallengeCategoryEntity.FORENSIC);
    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(CtfChallengeTypeEntity.DYNAMIC);
    MemberEntity creator = generateMemberEntity(MemberJobName.출제자, MemberTypeName.정회원,
        MemberRankName.일반회원);
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
        .creatorId(creator.getId())
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
        .andExpect(jsonPath("$.data.creatorId").value(creator.getId()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andExpect(jsonPath("$.data.dynamicInfo.maxScore").value(dynamicInfo.getMaxScore()))
        .andExpect(jsonPath("$.data.dynamicInfo.minScore").value(dynamicInfo.getMinScore()))
        .andExpect(jsonPath("$.data.flag").value(flag))
        .andDo(document("create-problem",
            relaxedRequestFields(
                fieldWithPath("title").description("문제 제목"),
                fieldWithPath("content").description("문제 내용"),
                fieldWithPath("contestId").description("문제가 등록 될 CTF id"),
                fieldWithPath("category.id").description("문제 카테고리 Id"),
                fieldWithPath("type.id").description("문제 Type Id"),
                fieldWithPath("isSolvable").description("현재 풀 수 있는 지 여부"),
                fieldWithPath("creatorId").description("문제 생성자 Id"),
                fieldWithPath("score").description("문제의 점수"),
                fieldWithPath("dynamicInfo.maxScore").description(
                        "TYPE이 DYNAMIC일 경우 maxScore")
                    .optional(),
                fieldWithPath("dynamicInfo.minScore").description(
                        "TYPE이 DYNAMIC일 경우 minScore")
                    .optional(),
                fieldWithPath("flag").description("문제의 flag")
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("회장 권한으로 STANDARD 문제 생성 - 성공")
  public void createStandardProblemSuccess() throws Exception {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        CtfChallengeCategoryEntity.FORENSIC);
    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(CtfChallengeTypeEntity.STANDARD);
    MemberEntity creator = generateMemberEntity(MemberJobName.출제자, MemberTypeName.정회원,
        MemberRankName.일반회원);
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
        .creatorId(creator.getId())
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
        .andExpect(jsonPath("$.data.creatorId").value(adminEntity.getId()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andExpect(jsonPath("$.data.flag").value(flag));
  }

  @Test
  @DisplayName("문제에 파일 등록 - 성공")
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
                parameterWithName("challengeId").description("등록할 문제 id")
            ),
            requestParts(
                partWithName("file").description("첨부 파일들 (form-data 에서 file= parameter 부분)")
            ),
            responseFields(
                generateFileDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("문제 오픈 - 성공")
  public void openProblemSuccess() throws Exception {

    MemberEntity creator = generateMemberEntity(MemberJobName.출제자, MemberTypeName.정회원,
        MemberRankName.일반회원);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.STANDARD,
        CtfChallengeCategoryEntity.MISC, score);
    generateCtfFlag(team, challenge);

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
        .andExpect(jsonPath("$.data.creatorId").value(challenge.getCreator().getId()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andDo(document("open-problem",
            pathParameters(
                parameterWithName("pid").description("등록할 문제 id")
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("문제 닫기 - 성공")
  public void closeProblemSuccess() throws Exception {

    MemberEntity creator = generateMemberEntity(MemberJobName.출제자, MemberTypeName.정회원,
        MemberRankName.일반회원);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.STANDARD,
        CtfChallengeCategoryEntity.MISC, score);
    generateCtfFlag(team, challenge);

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
        .andExpect(jsonPath("$.data.creatorId").value(challenge.getCreator().getId()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andDo(document("close-problem",
            pathParameters(
                parameterWithName("pid").description("등록할 문제 id")
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("회장 권한으로 문제 삭제 - 성공")
  public void deleteProblemSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(MemberJobName.출제자, MemberTypeName.정회원,
        MemberRankName.일반회원);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        CtfChallengeTypeEntity.STANDARD,
        CtfChallengeCategoryEntity.MISC, score);
    generateCtfFlag(team, challenge);

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
        .andExpect(jsonPath("$.data.creatorId").value(challenge.getCreator().getId()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andDo(document("delete-problem",
            pathParameters(
                parameterWithName("pid").description("삭제할 문제 id")
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("출제자 권한으로 문제 목록 불러오기 - 성공")
  public void getProblemListSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(MemberJobName.출제자, MemberTypeName.정회원,
        MemberRankName.일반회원);
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
    generateCtfFlag(team, challenge2);
    generateCtfFlag(team, challenge);

    // when
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    mockMvc.perform(multipart("/v1/admin/ctf/prob/file")
        .file(file)
        .param("challengeId", String.valueOf(challenge.getId()))
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
                parameterWithName("ctfId").description("문제 목록을 볼 CTF id")
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("출제자 권한으로 로그 목록 불러오기 - 성공")
  public void getSubmitLogListSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(MemberJobName.출제자, MemberTypeName.정회원,
        MemberRankName.일반회원);
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
    generateCtfFlag(team, challenge2);
    generateCtfFlag(team, challenge);

    generateCtfSubmitLog(team, creator, challenge, "이 바보야");
    generateCtfSubmitLog(team, adminEntity, challenge, "정현모 천재");
    generateCtfSubmitLog(team, creator, challenge2, "KEEPER 최고");
    generateCtfSubmitLog(team, adminEntity, challenge2, "너만 힘들어?");

    // then
    mockMvc.perform(get("/v1/admin/ctf/submit-log")
            .header("Authorization", probMakerToken)
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("get-submitLog-list",
            requestParameters(
                generateCommonPagingParameters("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                generateCtfSubmitLogDtoResponseFields(ResponseType.PAGE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }
}