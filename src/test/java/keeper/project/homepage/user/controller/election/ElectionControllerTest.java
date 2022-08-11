package keeper.project.homepage.user.controller.election;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import keeper.project.homepage.controller.election.ElectionSpringTestHelper;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.user.dto.election.request.ElectionVoteRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class ElectionControllerTest extends ElectionSpringTestHelper {

  private MemberEntity user;
  private MemberEntity admin;

  private String userToken;
  private String adminToken;

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(user);
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(admin);
  }

  @Test
  @DisplayName("[SUCCESS] 선거 목록 불러오기")
  public void getElections() throws Exception {
    boolean isAvailable = false;
    for (int i = 0; i < 7; i++) {
      generateElection(admin, isAvailable);
      isAvailable = !isAvailable;
    }

    String page = "0";
    String size = "14";

    mockMvc.perform(get("/v1/elections")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("page", page)
            .param("size", size))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.page.size").value(14))
        .andExpect(jsonPath("$.page.first").value(true))
        .andExpect(jsonPath("$.page.last").value(true))
        .andDo(document("election-lists",
            requestParameters(
                parameterWithName("page").description("선거 목록의 페이지 번호(default = 0)").optional(),
                parameterWithName("size").description("선거 목록 한 페이지의 개수(default = 10)").optional()
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("page.content[].electionId").description("선거 ID"),
                fieldWithPath("page.content[].name").description("선거 이름"),
                fieldWithPath("page.content[].description").description("선거에 대한 설명"),
                fieldWithPath("page.content[].registerTime").description("선거 등록 시간"),
                fieldWithPath("page.content[].creatorId").description("선거 제작자 멤버 ID"),
                fieldWithPath("page.content[].isAvailable").description("선거 오픈 여부"),
                fieldWithPath("page.empty").description("페이지가 비었는 지 여부"),
                fieldWithPath("page.first").description("첫 페이지 인지"),
                fieldWithPath("page.last").description("마지막 페이지 인지"),
                fieldWithPath("page.number").description("요소를 가져 온 페이지 번호 (0부터 시작)"),
                fieldWithPath("page.numberOfElements").description("요소 개수"),
                subsectionWithPath("page.pageable").description("해당 페이지에 대한 DB 정보"),
                fieldWithPath("page.size").description("요청한 페이지 크기"),
                subsectionWithPath("page.sort").description("정렬에 대한 정보"),
                fieldWithPath("page.totalElements").description("총 요소 개수"),
                fieldWithPath("page.totalPages").description("총 페이지")
            )));

  }

  @Test
  @DisplayName("[FAIL] 선거 목록 불러오기 - 권한 부족")
  public void getElectionsFailByAuth() throws Exception {
    boolean isAvailable = false;
    for (int i = 0; i < 7; i++) {
      generateElection(admin, isAvailable);
      isAvailable = !isAvailable;
    }

    String page = "0";
    String size = "14";
    String token = "fail";

    mockMvc.perform(get("/v1/elections")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .param("page", page)
            .param("size", size))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1003));
  }

  @Test
  @DisplayName("[SUCCESS] 선거 참여")
  public void joinElection() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    generateElectionVoter(user, election, false);

    mockMvc.perform(get("/v1/elections/join/{id}", election.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data").value(true))
        .andDo(document("election-join",
            pathParameters(
                parameterWithName("id").description("참여하고자 하는 선거 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("선거 참여 성공 여부")
            )));
  }

  @Test
  @DisplayName("[FAIL] 선거 참여 - 등록되지 않은 투표자")
  public void joinElectionFailByNotVoter() throws Exception {
    ElectionEntity election = generateElection(admin, true);

    mockMvc.perform(get("/v1/elections/join/{id}", election.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data").value(false));
  }

  @Test
  @DisplayName("[FAIL] 선거 참여 - 열리지 않은 투표")
  public void joinElectionFailByNotOpen() throws Exception {
    ElectionEntity election = generateElection(admin, false);
    generateElectionVoter(user, election, false);

    mockMvc.perform(get("/v1/elections/join/{id}", election.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data").value(false));
  }

  @Test
  @DisplayName("[SUCCESS] 선거 후보자 목록")
  public void getCandidates() throws Exception {
    ElectionEntity election1 = generateElection(admin, true);
    ElectionEntity election2 = generateElection(admin, true);
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회장").get();
    MemberEntity[] members = new MemberEntity[6];
    for (int i = 0; i < members.length; i++) {
      members[i] = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    }
    for (int i = 0; i < 3; i++) {
      generateElectionCandidate(members[i * 2], election1, memberJob);
      generateElectionCandidate(members[i * 2 + 1], election2, memberJob);
    }

    mockMvc.perform(get("/v1/elections/{eid}/jobs/{jid}", election1.getId(),
            memberJob.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("election-candidates",
            pathParameters(
                parameterWithName("eid").description("후보자 목록이 등록된 선거 ID"),
                parameterWithName("jid").description("후보자 목록이 등록된 직위 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].candidateId").description("후보자 테이블 ID"),
                fieldWithPath("list[].realName").description("후보자 실제 이름"),
                fieldWithPath("list[].thumbnailPath").description("후보자 썸네일 경로"),
                fieldWithPath("list[].generation").description("후보자 기수"),
                fieldWithPath("list[].description").description("후보자에 대한 설명"),
                fieldWithPath("list[].registerTime").description("후보자가 등록된 시간")
            )));
  }

  @Test
  @DisplayName("[FAIL] 선거 후보자 목록 - 등록되지 않은 선거")
  public void getCandidatesByNoneElection() throws Exception {
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회장").get();
    Long id = -1L;

    mockMvc.perform(get("/v1/elections/{eid}/jobs/{jid}", id, memberJob.getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 선거입니다."));
  }

  @Test
  @DisplayName("[FAIL] 선거 후보자 목록 - 등록되지 않은 직위")
  public void getCandidatesByNoneJob() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    Long id = -1L;

    mockMvc.perform(get("/v1/elections/{eid}/jobs/{jid}", election.getId(), id)
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-21))
        .andExpect(jsonPath("$.msg").value("ID가 " + id + "인 MemberJob이 존재하지 않습니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 투표")
  public void voteElection() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    generateElectionVoter(user, election, false);
    MemberJobEntity memberJob1 = memberJobRepository.findByName("ROLE_회장").get();
    MemberJobEntity memberJob2 = memberJobRepository.findByName("ROLE_부회장").get();
    ElectionCandidateEntity candidate1 = generateElectionCandidate(user, election, memberJob1);
    ElectionCandidateEntity candidate2 = generateElectionCandidate(user, election, memberJob2);

    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .electionId(election.getId())
        .voterId(user.getId())
        .candidateIds(new ArrayList<>(Arrays.asList(candidate1.getId(), candidate2.getId())))
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("election-vote",
            requestFields(
                fieldWithPath("voterId").description("투표자의 멤버 ID"),
                fieldWithPath("electionId").description("투표자가 투표하고자 하는 선거 ID"),
                fieldWithPath("candidateIds[]").description("투표자가 투표하는 후보자들의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("투표 성공 여부")
            )));
  }

  @Test
  @DisplayName("[FAIL] 투표 - 미입력된 정보")
  public void voteElectionFailByNullInfo() throws Exception {
    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(400));
  }

  @Test
  @DisplayName("[FAIL] 투표 - 존재하지 않는 멤버")
  public void voteElectionFailByNoneMember() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    Long id = -1L;

    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .electionId(election.getId())
        .voterId(id)
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 회원입니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 - 존재하지 않는 선거")
  public void voteElectionFailByNoneElection() throws Exception {
    Long id = -1L;

    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .electionId(id)
        .voterId(user.getId())
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 선거입니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 - 존재하지 않는 투표자")
  public void voteElectionFailByNoneVoter() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    MemberJobEntity memberJob1 = memberJobRepository.findByName("ROLE_회장").get();
    MemberJobEntity memberJob2 = memberJobRepository.findByName("ROLE_부회장").get();
    ElectionCandidateEntity candidate1 = generateElectionCandidate(user, election, memberJob1);
    ElectionCandidateEntity candidate2 = generateElectionCandidate(user, election, memberJob2);

    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .electionId(election.getId())
        .voterId(user.getId())
        .candidateIds(new ArrayList<>(Arrays.asList(candidate1.getId(), candidate2.getId())))
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14002))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 투표자입니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 - 닫힌 선거")
  public void voteElectionFailByCloseElection() throws Exception {
    ElectionEntity election = generateElection(admin, false);
    generateElectionVoter(user, election, false);
    MemberJobEntity memberJob1 = memberJobRepository.findByName("ROLE_회장").get();
    MemberJobEntity memberJob2 = memberJobRepository.findByName("ROLE_부회장").get();
    ElectionCandidateEntity candidate1 = generateElectionCandidate(user, election, memberJob1);
    ElectionCandidateEntity candidate2 = generateElectionCandidate(user, election, memberJob2);

    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .electionId(election.getId())
        .voterId(user.getId())
        .candidateIds(new ArrayList<>(Arrays.asList(candidate1.getId(), candidate2.getId())))
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14007))
        .andExpect(jsonPath("$.msg").value("닫힌 선거에 대해서는 접근할 수 없습니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 - 투표해야 하는 직위 개수와 불일치")
  public void voteElectionFailByNotMatchCount() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    generateElectionVoter(user, election, false);
    MemberJobEntity memberJob1 = memberJobRepository.findByName("ROLE_회장").get();
    MemberJobEntity memberJob2 = memberJobRepository.findByName("ROLE_부회장").get();
    ElectionCandidateEntity candidate1 = generateElectionCandidate(user, election, memberJob1);
    generateElectionCandidate(user, election, memberJob2);

    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .electionId(election.getId())
        .voterId(user.getId())
        .candidateIds(new ArrayList<>(Collections.singletonList(candidate1.getId())))
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14005))
        .andExpect(jsonPath("$.msg").value("투표해야 하는 후보자 수와 일치하지 않습니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 - 중복된 직위에 대한 투표")
  public void voteElectionFailByDuplicationJobVote() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    generateElectionVoter(user, election, false);
    MemberJobEntity memberJob1 = memberJobRepository.findByName("ROLE_회장").get();
    MemberJobEntity memberJob2 = memberJobRepository.findByName("ROLE_부회장").get();
    ElectionCandidateEntity candidate1 = generateElectionCandidate(user, election, memberJob1);
    generateElectionCandidate(user, election, memberJob2);

    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .electionId(election.getId())
        .voterId(user.getId())
        .candidateIds(new ArrayList<>(Arrays.asList(candidate1.getId(), candidate1.getId())))
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14006))
        .andExpect(jsonPath("$.msg").value("동일한 직위에 대한 투표가 존재합니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 - 해당 선거 후보자가 아닌 후보자가 존재")
  public void voteElectionFailByNotMatchCandidateAboutElection() throws Exception {
    ElectionEntity election1 = generateElection(admin, true);
    ElectionEntity election2 = generateElection(admin, true);
    generateElectionVoter(user, election1, false);
    MemberJobEntity memberJob1 = memberJobRepository.findByName("ROLE_회장").get();
    MemberJobEntity memberJob2 = memberJobRepository.findByName("ROLE_부회장").get();
    ElectionCandidateEntity candidate1 = generateElectionCandidate(user, election1, memberJob1);
    generateElectionCandidate(user, election1, memberJob2);
    ElectionCandidateEntity candidate2 = generateElectionCandidate(user, election2, memberJob1);

    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .electionId(election1.getId())
        .voterId(user.getId())
        .candidateIds(new ArrayList<>(Arrays.asList(candidate1.getId(), candidate2.getId())))
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14008))
        .andExpect(jsonPath("$.msg").value("해당하는 선거의 후보자가 아닙니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 - 이미 투표한 투표자")
  public void voteElectionFailByAlreadyVoted() throws Exception {
    ElectionEntity election1 = generateElection(admin, true);
    ElectionEntity election2 = generateElection(admin, true);
    generateElectionVoter(user, election1, true);
    MemberJobEntity memberJob1 = memberJobRepository.findByName("ROLE_회장").get();
    MemberJobEntity memberJob2 = memberJobRepository.findByName("ROLE_부회장").get();
    ElectionCandidateEntity candidate1 = generateElectionCandidate(user, election1, memberJob1);
    generateElectionCandidate(user, election1, memberJob2);
    ElectionCandidateEntity candidate2 = generateElectionCandidate(user, election2, memberJob1);

    ElectionVoteRequestDto request = ElectionVoteRequestDto.builder()
        .electionId(election1.getId())
        .voterId(user.getId())
        .candidateIds(new ArrayList<>(Arrays.asList(candidate1.getId(), candidate2.getId())))
        .build();

    mockMvc.perform(post("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14009))
        .andExpect(jsonPath("$.msg").value("이미 투표를 진행한 투표자입니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 투표 여부 조회")
  public void isVoted() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    generateElectionVoter(user, election, true);

    mockMvc.perform(get("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("electionId", String.valueOf(election.getId()))
            .param("voterId", String.valueOf(user.getId())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("election-isVoted",
            requestParameters(
                parameterWithName("electionId").description("투표 여부를 조회할 선거 ID"),
                parameterWithName("voterId").description("투표 여부를 조회할 멤버 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("투표 여부")
            )));
  }

  @Test
  @DisplayName("[FAIL] 투표 여부 조회 - 존재하지 않는 투표")
  public void isVotedFailByNoneElection() throws Exception {
    Long id = -1L;

    mockMvc.perform(get("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("electionId", String.valueOf(id))
            .param("voterId", String.valueOf(user.getId())))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 선거입니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 여부 조회 - 존재하지 않는 멤버")
  public void isVotedFailByNoneMember() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    Long id = -1L;

    mockMvc.perform(get("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("electionId", String.valueOf(election.getId()))
            .param("voterId", String.valueOf(id)))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 회원입니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 여부 조회 - 존재하지 않는 투표자")
  public void isVotedFailByNoneVoter() throws Exception {
    ElectionEntity election = generateElection(admin, true);

    mockMvc.perform(get("/v1/elections/votes")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("electionId", String.valueOf(election.getId()))
            .param("voterId", String.valueOf(user.getId())))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14002))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 투표자입니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 투표 결과 조회")
  public void countVotes() throws Exception {
    ElectionEntity election1 = generateElection(admin, true);
    ElectionEntity election2 = generateElection(admin, true);
    MemberJobEntity memberJob1 = memberJobRepository.findByName("ROLE_회장").get();
    MemberJobEntity memberJob2 = memberJobRepository.findByName("ROLE_부회장").get();
    ElectionCandidateEntity candidate1 = generateElectionCandidate(admin, election1, memberJob1);
    ElectionCandidateEntity candidate2 = generateElectionCandidate(user, election1, memberJob1);
    ElectionCandidateEntity candidate3 = generateElectionCandidate(admin, election2, memberJob1);
    ElectionCandidateEntity candidate4 = generateElectionCandidate(user, election1, memberJob2);
    for (int i = 0; i < 3; i++) {
      generateElectionChartLog(candidate1);
    }
    generateElectionChartLog(candidate2);
    generateElectionChartLog(candidate1);
    generateElectionChartLog(candidate3);
    generateElectionChartLog(candidate4);
    for (int i = 0; i < 2; i++) {
      generateElectionChartLog(candidate2);
    }

    election1.closeElection();

    mockMvc.perform(get("/v1/elections/results")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("electionId", String.valueOf(election1.getId()))
            .param("jobId", String.valueOf(memberJob1.getId())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("election-results",
            requestParameters(
                parameterWithName("electionId").description("투표 결과를 확인하고자 하는 선거 ID"),
                parameterWithName("jobId").description("투표 결과를 확인하고자 하는 직위 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].memberId").description("득표한 선거 후보자 멤버 ID"),
                fieldWithPath("list[].name").description("득표한 선거 후보자 실제 이름")
            )));
  }


  @Test
  @DisplayName("[FAIL] 투표 결과 조회 - 존재하지 않는 선거")
  public void countVotesFailByNoneElection() throws Exception {
    Long id = -1L;
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회장").get();

    mockMvc.perform(get("/v1/elections/results")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("electionId", String.valueOf(id))
            .param("jobId", String.valueOf(memberJob.getId())))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 선거입니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 결과 조회 - 존재하지 않는 직위")
  public void countVotesFailByNoneJob() throws Exception {
    Long id = -1L;
    ElectionEntity election = generateElection(admin, true);

    mockMvc.perform(get("/v1/elections/results")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("electionId", String.valueOf(election.getId()))
            .param("jobId", String.valueOf(id)))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-21))
        .andExpect(jsonPath("$.msg").value("ID가 " + id + "인 MemberJob이 존재하지 않습니다."));
  }

  @Test
  @DisplayName("[FAIL] 투표 결과 조회 - 열려 있는 투표")
  public void countVotesFailByOpenElection() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회장").get();

    mockMvc.perform(get("/v1/elections/results")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("electionId", String.valueOf(election.getId()))
            .param("jobId", String.valueOf(memberJob.getId())))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14010))
        .andExpect(jsonPath("$.msg").value("종료되지 않은 투표입니다."));
  }
}
