package keeper.project.homepage.ctf.controller;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.출제자;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회장;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.ctf.dto.CtfCommonChallengeDto.DEFAULT_SUBMIT_COUNT;
import static keeper.project.homepage.ctf.dto.CtfCommonChallengeDto.MAX_SUBMIT_COUNT;
import static keeper.project.homepage.ctf.dto.CtfCommonChallengeDto.MIN_SUBMIT_COUNT;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.FORENSIC;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.MISC;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.DYNAMIC;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.STANDARD;
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

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ctf.dto.CtfChallengeAdminDto;
import keeper.project.homepage.ctf.dto.CtfChallengeCategoryDto;
import keeper.project.homepage.ctf.dto.CtfChallengeDto;
import keeper.project.homepage.ctf.dto.CtfChallengeTypeDto;
import keeper.project.homepage.ctf.dto.CtfCommonChallengeDto;
import keeper.project.homepage.ctf.dto.CtfContestAdminDto;
import keeper.project.homepage.ctf.dto.CtfDynamicChallengeInfoDto;
import keeper.project.homepage.ctf.dto.CtfProbMakerDto;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfSubmitLogEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.util.service.CtfUtilService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    userEntity = generateMemberEntity(회원, 정회원, 일반회원);
    userToken = generateJWTToken(userEntity);
    adminEntity = generateMemberEntity(회장, 정회원, 우수회원);
    adminToken = generateJWTToken(adminEntity);
    makeProbUserEntity = generateMemberEntity(출제자, 정회원, 일반회원);
    makeProbUserToken = generateJWTToken(makeProbUserEntity);
    contestEntity = generateCtfContest(adminEntity);
  }

  @Test
  @DisplayName("회장 권한으로 CTF 주최 - 성공")
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
                generateCommonPagingParameters("한 페이지당 출력 수(default = 10")
            ),
            responseFields(
                generateContestDtoResponseFields(ResponseType.PAGE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("회장 권한으로 문제 출제자 지정 - 성공")
  public void designateProbMakerSuccess() throws Exception {
    MemberEntity probMaker = generateMemberEntity(회원, 정회원, 일반회원);
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
  @DisplayName("회장 권한으로 문제 출제자 삭제 - 성공")
  public void disqualifyProbMakerSuccess() throws Exception {
    // given
    MemberEntity probMaker = generateMemberEntity(회원, 정회원, 일반회원);
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
                fieldWithPath("memberId").description("출제자를 삭제할 member의 Id")
            ),
            responseFields(
                generateCommonResponseFields("성공: true +\n실패: false", "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
    Assertions.assertThat(isDisqualifyProbMakerRole(probMaker, probMakerRole)).isTrue();
  }

  private boolean isDisqualifyProbMakerRole(MemberEntity probMaker, MemberJobEntity probMakerRole) {
    return memberHasMemberJobRepository.findAllByMemberEntity_IdAndAndMemberJobEntity_Id(
        probMaker.getId(), probMakerRole.getId()).size() == 0;
  }

  @Test
  @DisplayName("출제자 권한으로 문제 생성 - 성공")
  public void createProblemSuccess() throws Exception {
    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(FORENSIC);
    categories.add(MISC);

    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(
        ctfChallengeTypeRepository.getById(DYNAMIC.getId()));
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
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
    CtfChallengeAdminDto challenge = generateCtfChallengeAdminDto(35L,
        categories, type, creator, title, content, isSolvable, score, flag, dynamicInfo);

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
        .andExpect(jsonPath("$.data.categories[0].id").value(categories.get(0).getId()))
        .andExpect(jsonPath("$.data.categories[1].id").value(categories.get(1).getId()))
        .andExpect(jsonPath("$.data.type.id").value(type.getId()))
        .andExpect(jsonPath("$.data.isSolvable").value(isSolvable))
        .andExpect(jsonPath("$.data.creatorName").value(creator.getNickName()))
        .andExpect(jsonPath("$.data.score").value(dynamicInfo.getMaxScore()))
        .andExpect(jsonPath("$.data.dynamicInfo.maxScore").value(dynamicInfo.getMaxScore()))
        .andExpect(jsonPath("$.data.dynamicInfo.minScore").value(dynamicInfo.getMinScore()))
        .andExpect(jsonPath("$.data.flag").value(flag))
        .andDo(document("create-problem",
            relaxedRequestFields(
                fieldWithPath("title").description("문제 제목"),
                fieldWithPath("content").description("문제 내용"),
                fieldWithPath("contestId").description("문제가 등록 될 CTF id"),
                fieldWithPath("categories[].id").description("문제 카테고리 Id"),
                fieldWithPath("type.id").description("문제 Type Id"),
                fieldWithPath("isSolvable").description("현재 풀 수 있는 지 여부"),
                fieldWithPath("score").description(
                    "문제의 점수 (TYPE이 DYNAMIC일 경우 아무 값이나 보내주시면 됩니다. 초기 값은 maxScore로 저장됩니다.)"),
                fieldWithPath("dynamicInfo.maxScore").description(
                        "TYPE이 DYNAMIC일 경우 maxScore")
                    .optional(),
                fieldWithPath("dynamicInfo.minScore").description(
                        "TYPE이 DYNAMIC일 경우 minScore")
                    .optional(),
                fieldWithPath("flag").description("문제의 flag"),
                fieldWithPath("maxSubmitCount").description(
                        String.format(
                            "각 팀당 가능한 최대 제출 횟수 (%d이상 %d이하)%n%n만약 null로 보낸다면 DEFAULT 값(%d)이 들어갑니다.%n%n"
                                + "최대, 최소값을 벗어나는 제출 횟수를 입력할 경우 success:false, code:400과 함께 msg에 에러 메시지가 담겨 나갑니다.",
                            MIN_SUBMIT_COUNT, MAX_SUBMIT_COUNT, DEFAULT_SUBMIT_COUNT))
                    .optional()
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

    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(FORENSIC);

    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(
        ctfChallengeTypeRepository.getById(STANDARD.getId()));
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    Long teamScore = 0L;
    generateCtfTeam(contestEntity, creator, teamScore);

    String title = "test_title";
    String content = "test_content";
    Boolean isSolvable = true;
    Long score = 1234L;
    String flag = "flag{keeper}";
    CtfChallengeAdminDto challenge = generateCtfChallengeAdminDto(35L,
        categories, type, creator, title, content, isSolvable, score, flag);

    createChallengeControllerTest(challenge)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(title))
        .andExpect(jsonPath("$.data.content").value(content))
        .andExpect(jsonPath("$.data.contestId").value(contestEntity.getId()))
        .andExpect(jsonPath("$.data.categories[0].id").value(categories.get(0).getId()))
        .andExpect(jsonPath("$.data.type.id").value(type.getId()))
        .andExpect(jsonPath("$.data.isSolvable").value(isSolvable))
        .andExpect(jsonPath("$.data.creatorName").value(adminEntity.getNickName()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andExpect(jsonPath("$.data.flag").value(flag));
  }

  @Test
  @DisplayName("문제 생성 시 maxSubmitCount를 넣지 않을 경우 default value가 들어가야 한다.")
  public void createProblem_maxSubmitCount_defaultValue() throws Exception {
    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(FORENSIC);

    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(
        ctfChallengeTypeRepository.getById(STANDARD.getId()));
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    Long teamScore = 0L;
    generateCtfTeam(contestEntity, creator, teamScore);

    String title = "test_title";
    String content = "test_content";
    Boolean isSolvable = true;
    Long score = 1234L;
    String flag = "flag{keeper}";

    CtfChallengeAdminDto challenge = generateCtfChallengeAdminDto(
        null, categories, type, creator, title, content, isSolvable, score, flag);

    createChallengeControllerTest(challenge)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(title))
        .andExpect(jsonPath("$.data.content").value(content))
        .andExpect(jsonPath("$.data.contestId").value(contestEntity.getId()))
        .andExpect(jsonPath("$.data.categories[0].id").value(categories.get(0).getId()))
        .andExpect(jsonPath("$.data.type.id").value(type.getId()))
        .andExpect(jsonPath("$.data.isSolvable").value(isSolvable))
        .andExpect(jsonPath("$.data.creatorName").value(adminEntity.getNickName()))
        .andExpect(jsonPath("$.data.score").value(score))
        .andExpect(jsonPath("$.data.flag").value(flag))
        .andExpect(jsonPath("$.data.maxSubmitCount").value(DEFAULT_SUBMIT_COUNT));
  }

  @NonNull
  private ResultActions createChallengeControllerTest(CtfChallengeAdminDto challenge)
      throws Exception {
    return mockMvc.perform(post("/v1/admin/ctf/prob")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(challenge)))
        .andDo(print());
  }

  @ParameterizedTest
  @ValueSource(longs = {-10, 0, 1, 3, 15, 45, 50, 51, 123, Integer.MAX_VALUE})
  @DisplayName("회장 권한으로 STANDARD 문제 생성 - 성공")
  public void createProblem_maxSubmitCount_validValue(long maxSubmitCount) throws Exception {

    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(FORENSIC);

    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(
        ctfChallengeTypeRepository.getById(STANDARD.getId()));
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    Long teamScore = 0L;
    generateCtfTeam(contestEntity, creator, teamScore);

    String title = "test_title";
    String content = "test_content";
    Boolean isSolvable = true;
    Long score = 1234L;
    String flag = "flag{keeper}";
    CtfChallengeAdminDto challenge = generateCtfChallengeAdminDto(
        maxSubmitCount, categories, type, creator, title, content, isSolvable, score, flag);

    if (MIN_SUBMIT_COUNT <= maxSubmitCount && maxSubmitCount <= MAX_SUBMIT_COUNT) {
      createChallengeControllerTest(challenge)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.code").value(0))
          .andExpect(jsonPath("$.data.title").value(title))
          .andExpect(jsonPath("$.data.content").value(content))
          .andExpect(jsonPath("$.data.contestId").value(contestEntity.getId()))
          .andExpect(jsonPath("$.data.categories[0].id").value(categories.get(0).getId()))
          .andExpect(jsonPath("$.data.type.id").value(type.getId()))
          .andExpect(jsonPath("$.data.isSolvable").value(isSolvable))
          .andExpect(jsonPath("$.data.creatorName").value(adminEntity.getNickName()))
          .andExpect(jsonPath("$.data.score").value(score))
          .andExpect(jsonPath("$.data.flag").value(flag));
    } else {
      createChallengeControllerTest(challenge)
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.code").value(400));
    }

  }

  private CtfChallengeAdminDto generateCtfChallengeAdminDto(Long maxSubmitCount,
      List<CtfChallengeCategory> categories, CtfChallengeTypeDto type, MemberEntity creator,
      String title, String content, Boolean isSolvable, Long score, String flag) {
    return generateCtfChallengeAdminDto(
        maxSubmitCount, categories, type, creator, title, content, isSolvable, score, flag, null);
  }

  private CtfChallengeAdminDto generateCtfChallengeAdminDto(Long maxSubmitCount,
      List<CtfChallengeCategory> categories, CtfChallengeTypeDto type, MemberEntity creator,
      String title, String content, Boolean isSolvable, Long score, String flag,
      CtfDynamicChallengeInfoDto dynamicInfo) {

    List<CtfChallengeCategoryDto> categoryDtos = categories
        .stream()
        .map(ctfChallengeCategory -> CtfChallengeCategoryDto
            .builder()
            .id(ctfChallengeCategory.getId())
            .name(ctfChallengeCategory.getName()).build())
        .toList();

    CtfChallengeAdminDto challenge = CtfChallengeAdminDto.builder()
        .challengeDto(CtfChallengeDto.builder()
            .commonChallengeDto(CtfCommonChallengeDto.builder()
                .title(title)
                .contestId(contestEntity.getId())
                .categories(categoryDtos)
                .score(score)
                .maxSubmitCount(maxSubmitCount)
                .build()
            )
            .content(content)
            .creatorName(creator.getNickName())
            .build()
        )
        .type(type)
        .isSolvable(isSolvable)
        .flag(flag)
        .dynamicInfo(dynamicInfo)
        .build();
    return challenge;
  }

  @Test
  @DisplayName("문제에 파일 등록 - 성공")
  public void fileRegistrationInProblemSuccess() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    Long score = 1234L;

    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(FORENSIC);

    CtfChallengeEntity challenge = generateCtfChallenge(
        contestEntity, DYNAMIC, categories, score, true);
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
    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(FORENSIC);
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    CtfChallengeEntity challenge = generateCtfChallenge(
        contestEntity, STANDARD, categories, score, false);
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
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(MISC);
    CtfChallengeEntity challenge = generateCtfChallenge(
        contestEntity, STANDARD, categories, score, false);
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
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(MISC);
    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity, STANDARD, categories, score,
        true);
    generateCtfFlag(team, challenge, false);

    // when
    insertDummyFileInChallenge(challenge);

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
        .andExpect(jsonPath("$.data.score").value(0))
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
  @DisplayName("문제 삭제 시 해당 문제를 푼 팀의 점수 롤백 (STANDARD TYPE) - 성공")
  public void deleteStandardProblemScoreRollbackSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    creator.addMemberJob(memberJobRepository.findByName("ROLE_회원").get());
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);

    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(MISC);

    CtfChallengeEntity challenge = generateCtfChallenge(
        contestEntity, STANDARD, categories, score, true);
    CtfFlagEntity flag = generateCtfFlag(team, challenge, false);

    // when
    insertDummyFileInChallenge(challenge);
    solveChallenge(challenge, flag, generateJWTToken(creator));

    // then
    Long scoreBeforeDelete = ctfTeamRepository.getById(team.getId()).getScore();
    Assertions.assertThat(scoreBeforeDelete).isEqualTo(score);

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
        .andExpect(jsonPath("$.data.score").value(0));

    Long scoreAfterDelete = ctfTeamRepository.getById(team.getId()).getScore();
    Assertions.assertThat(scoreAfterDelete).isEqualTo(0);
  }

  @Test
  @DisplayName("문제 삭제 시 해당 문제를 푼 팀의 점수 롤백 (DYNAMIC TYPE) - 성공")
  public void deleteDynamicProblemScoreRollbackSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    creator.addMemberJob(memberJobRepository.findByName("ROLE_회원").get());
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);

    List<CtfChallengeCategory> categories = new ArrayList<>();
    categories.add(MISC);

    CtfChallengeEntity challenge = generateCtfChallenge(contestEntity,
        DYNAMIC, categories, score, true);
    Long maxScore = 2000L;
    Long minScore = 100L;
    generateDynamicChallengeInfo(challenge, maxScore, minScore);
    CtfFlagEntity flag = generateCtfFlag(team, challenge, false);

    // when
    insertDummyFileInChallenge(challenge);
    solveChallenge(challenge, flag, generateJWTToken(creator));

    // then
    Long scoreBeforeDelete = ctfTeamRepository.getById(team.getId()).getScore();
    Assertions.assertThat(scoreBeforeDelete).isEqualTo(minScore);

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
        .andExpect(jsonPath("$.data.score").value(0));

    Long scoreAfterDelete = ctfTeamRepository.getById(team.getId()).getScore();
    Assertions.assertThat(scoreAfterDelete).isEqualTo(0);
  }

  private void solveChallenge(CtfChallengeEntity challenge, CtfFlagEntity flag, String authToken)
      throws Exception {
    String content = "{\n"
        + "    \"content\": \"" + flag.getContent() + "\"\n"
        + "}";
    mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/ctf/prob/{pid}/submit/flag",
                String.valueOf(challenge.getId()))
            .header("Authorization", authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andDo(print());
  }

  private void insertDummyFileInChallenge(CtfChallengeEntity challenge) throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    mockMvc.perform(multipart("/v1/admin/ctf/prob/file")
        .file(file)
        .param("challengeId", String.valueOf(challenge.getId()))
        .header("Authorization", adminToken)
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  @Test
  @DisplayName("출제자 권한으로 문제 목록 불러오기 - 성공")
  public void getProblemListSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    String probMakerToken = generateJWTToken(creator);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);

    List<CtfChallengeCategory> categories1 = new ArrayList<>();
    categories1.add(MISC);

    CtfChallengeEntity challenge1 = generateCtfChallenge(
        contestEntity, STANDARD, categories1, score, false);

    List<CtfChallengeCategory> categories2 = new ArrayList<>();
    categories2.add(FORENSIC);

    CtfChallengeEntity challenge2 = generateCtfChallenge(
        contestEntity, DYNAMIC, categories2, score, false);
    generateDynamicChallengeInfo(challenge2, 1000L, 100L);
    generateCtfFlag(team, challenge2, false);
    generateCtfFlag(team, challenge1, false);

    // when
    MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png",
        "<<png data>>".getBytes());
    mockMvc.perform(multipart("/v1/admin/ctf/prob/file")
        .file(file)
        .param("challengeId", String.valueOf(challenge1.getId()))
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
                generateCommonPagingParameters("한 페이지당 출력 수(default = 10)",
                    parameterWithName("ctfId").description("문제 목록을 볼 CTF id"))
            ),
            responseFields(
                generateChallengeAdminDtoResponseFields(ResponseType.PAGE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("출제자 권한으로 로그 목록 불러오기 - 성공")
  public void getSubmitLogListSuccess() throws Exception {
    // given
    MemberEntity creator = generateMemberEntity(출제자, 정회원, 일반회원);
    String probMakerToken = generateJWTToken(creator);
    Long teamScore = 0L;
    Long score = 1234L;
    CtfTeamEntity team = generateCtfTeam(contestEntity, creator, teamScore);
    List<CtfChallengeCategory> categories1 = new ArrayList<>();
    categories1.add(MISC);

    CtfChallengeEntity challenge = generateCtfChallenge(
        contestEntity, STANDARD, categories1, score, false);

    List<CtfChallengeCategory> categories2 = new ArrayList<>();
    categories2.add(FORENSIC);
    CtfChallengeEntity challenge2 = generateCtfChallenge(
        contestEntity, DYNAMIC, categories2, score, false);
    generateDynamicChallengeInfo(challenge2, 1000L, 100L);
    generateCtfFlag(team, challenge2, false);
    generateCtfFlag(team, challenge, false);

    CtfSubmitLogEntity log1 = generateCtfSubmitLog(team, creator, challenge, "이 바보야");
    CtfSubmitLogEntity log2 = generateCtfSubmitLog(team, adminEntity, challenge, "정현모 천재");
    CtfSubmitLogEntity log3 = generateCtfSubmitLog(team, creator, challenge2, "KEEPER 최고");
    CtfSubmitLogEntity log4 = generateCtfSubmitLog(team, adminEntity, challenge2, "너만 힘들어?");

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
                generateCommonPagingParameters("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                generateCtfSubmitLogDtoResponseFields(ResponseType.PAGE,
                    "성공: true +\n실패: false", "성공 시 0을 반환", "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }
}