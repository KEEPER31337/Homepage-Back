package keeper.project.homepage.admin.controller.election;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.admin.dto.election.request.ElectionCandidateCreateRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCreateRequestDto;
import keeper.project.homepage.controller.election.ElectionSpringTestHelper;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class AdminElectionControllerTest extends ElectionSpringTestHelper {

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
  @DisplayName("[SUCCESS] 선거 개설")
  public void setUpElection() throws Exception {
    String electionName = "회장 선거";
    String description = "새로운 회장 선출을 위한 투표";
    Boolean isAvailable = true;

    ElectionCreateRequestDto electionCreateRequestDto = ElectionCreateRequestDto.builder()
        .name(electionName)
        .description(description)
        .isAvailable(isAvailable)
        .build();

    mockMvc.perform(post("/v1/admin/elections")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(electionCreateRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data").isNumber())
        .andDo(document("election-create",
            requestFields(
                fieldWithPath("name").description("생성할 선거의 이름"),
                fieldWithPath("description").description("생성할 선거의 설명"),
                fieldWithPath("registerTime").description("선거 생성 시간(서버에서 입력)"),
                fieldWithPath("isAvailable").description("생성할 선거의 오픈 여부")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("생성에 성공한 선거의 ID")
            )));
  }

  @Test
  @DisplayName("[FAIL] 선거 개설 - 일반 회원 접근")
  public void setUpElectionFailByAuth() throws Exception {
    String electionName = "회장 선거";
    String description = "새로운 회장 선출을 위한 투표";
    Boolean isAvailable = true;

    ElectionCreateRequestDto electionCreateRequestDto = ElectionCreateRequestDto.builder()
        .name(electionName)
        .description(description)
        .isAvailable(isAvailable)
        .build();

    mockMvc.perform(post("/v1/admin/elections")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(electionCreateRequestDto)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1003))
        .andExpect(jsonPath("$.msg").value("접근이 거부되었습니다."));
  }

  @Test
  @DisplayName("[FAIL] 선거 개설 - 선거 이름, 진행 여부 미입력")
  public void setUpElectionFailByNotInput() throws Exception {
    String description = "새로운 회장 선출을 위한 투표";

    ElectionCreateRequestDto electionCreateRequestDto = ElectionCreateRequestDto.builder()
        .description(description)
        .build();

    mockMvc.perform(post("/v1/admin/elections")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(electionCreateRequestDto)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(400));
  }

  @Test
  @DisplayName("[FAIL] 선거 개설 - 선거 이름 길이 초과")
  public void setUpElectionFailByLength() throws Exception {
    String electionName = "123456789123456789123456789123456789123456789123456789";
    String description = "새로운 회장 선출을 위한 투표";
    Boolean isAvailable = true;

    ElectionCreateRequestDto electionCreateRequestDto = ElectionCreateRequestDto.builder()
        .name(electionName)
        .description(description)
        .isAvailable(isAvailable)
        .build();

    mockMvc.perform(post("/v1/admin/elections")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(electionCreateRequestDto)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(400));
  }

  @Test
  @DisplayName("[SUCCESS] 선거 삭제")
  public void deleteElection() throws Exception {
    ElectionEntity election = generateElection(admin, false);

    mockMvc.perform(delete("/v1/admin/elections/{id}", election.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("election-delete",
            pathParameters(
                parameterWithName("id").description("삭제하고자 하는 선거 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.electionId").description("삭제에 성공한 선거 ID"),
                fieldWithPath("data.name").description("삭제에 성공한 선거 이름")
            )));
  }

  @Test
  @DisplayName("[FAIL] 선거 삭제 - 존재하지 않는 선거")
  public void deleteElectionFailByNoneElection() throws Exception {
    Long id = -1L;

    mockMvc.perform(delete("/v1/admin/elections/{id}", id)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 선거입니다."));
  }

  @Test
  @DisplayName("[FAIL] 선거 삭제 - 종료되지 않은 선거")
  public void deleteElectionFailByNotClose() throws Exception {
    ElectionEntity election = generateElection(admin, true);

    mockMvc.perform(delete("/v1/admin/elections/{id}", election.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14010))
        .andExpect(jsonPath("$.msg").value("종료되지 않은 투표입니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 선거 오픈")
  public void openElection() throws Exception {
    ElectionEntity election = generateElection(admin, false);

    mockMvc.perform(patch("/v1/admin/elections/{id}/open", election.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data.electionId").value(election.getId()))
        .andExpect(jsonPath("$.data.name").value(election.getName()))
        .andExpect(jsonPath("$.data.isAvailable").value(true))
        .andDo(document("election-open",
            pathParameters(
                parameterWithName("id").description("오픈하고자 하는 선거의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.electionId").description("오픈한 선거의 ID"),
                fieldWithPath("data.name").description("오픈한 선거의 이름"),
                fieldWithPath("data.isAvailable").description("오픈 여부")
            )));
  }

  @Test
  @DisplayName("[FAIL] 선거 오픈 - 존재하지 않는 선거")
  public void openElectionFailByNotFoundElection() throws Exception {
    Long electionId = -1L;

    mockMvc.perform(patch("/v1/admin/elections/{id}/open", electionId)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 선거입니다."));
  }

  @Test
  @DisplayName("[FAIL] 선거 오픈 - 문자열 PATH")
  public void openElectionFailByStringPath() throws Exception {
    String path = "path";

    mockMvc.perform(patch("/v1/admin/elections/{id}/open", path)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-9998))
        .andExpect(jsonPath("$.msg").value("파라미터 타입이 일치하지 않습니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 선거 종료")
  public void closeElection() throws Exception {
    ElectionEntity election = generateElection(admin, true);

    mockMvc.perform(patch("/v1/admin/elections/{id}/close", election.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data.electionId").value(election.getId()))
        .andExpect(jsonPath("$.data.name").value(election.getName()))
        .andExpect(jsonPath("$.data.isAvailable").value(false))
        .andDo(document("election-close",
            pathParameters(
                parameterWithName("id").description("종료하고자 하는 선거의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.electionId").description("종료한 선거의 ID"),
                fieldWithPath("data.name").description("종료한 선거의 이름"),
                fieldWithPath("data.isAvailable").description("오픈 여부")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 선거 후보자 단일 등록")
  public void registerCandidate() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    ElectionCandidateCreateRequestDto request = ElectionCandidateCreateRequestDto.builder()
        .memberId(admin.getId())
        .description("후보")
        .electionId(election.getId())
        .memberJobId(1L)
        .build();

    mockMvc.perform(post("/v1/admin/elections/candidate")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("election-register-candidate",
            requestFields(
                fieldWithPath("memberId").description("후보자로 등록할 멤버의 ID"),
                fieldWithPath("description").description("후보자로 등록할 멤버의 정보"),
                fieldWithPath("electionId").description("후보자가 등록될 선거의 ID"),
                fieldWithPath("memberJobId").description("후보자가 등록될 직위의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("등록에 성공한 선거 후보자 테이블 ID")
            )));
  }

  @Test
  @DisplayName("[FAIL] 선거 후보자 단일 등록 - 미입력 정보")
  public void registerCandidateFailByNullInfo() throws Exception {
    ElectionCandidateCreateRequestDto request = ElectionCandidateCreateRequestDto.builder()
        .description("후보")
        .build();

    mockMvc.perform(post("/v1/admin/elections/candidate")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(400));
  }

  @Test
  @DisplayName("[FAIL] 선거 후보자 단일 등록 - 이미 존재하는 선거 후보자")
  public void registerCandidateFailByExist() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회장").get();
    generateElectionCandidate(admin, election, memberJob);
    ElectionCandidateCreateRequestDto request = ElectionCandidateCreateRequestDto.builder()
        .memberId(admin.getId())
        .description("후보")
        .electionId(election.getId())
        .memberJobId(1L)
        .build();

    mockMvc.perform(post("/v1/admin/elections/candidate")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14003))
        .andExpect(jsonPath("$.msg").value("이미 등록된 후보자입니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 선거 후보자 단일 삭제")
  public void deleteCandidate() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    MemberJobEntity memberJob = memberJobRepository.findByName("ROLE_회장").get();
    ElectionCandidateEntity electionCandidate = generateElectionCandidate(user, election,
        memberJob);
    ElectionCandidateEntity saved = electionCandidateRepository.save(electionCandidate);

    mockMvc.perform(delete("/v1/admin/elections/candidate/{id}", saved.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.deletedId").value(saved.getId()))
        .andExpect(jsonPath("$.data.candidateMemberId").value(saved.getCandidate().getId()))
        .andExpect(jsonPath("$.data.electionId").value(saved.getElection().getId()))
        .andExpect(jsonPath("$.data.memberJobId").value(saved.getMemberJob().getId()))
        .andDo(document("election-delete-candidate",
            pathParameters(
                parameterWithName("id").description("삭제하고자 하는 후보자 테이블 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.deletedId").description("삭제에 성공한 후보자 테이블 ID"),
                fieldWithPath("data.candidateMemberId").description("삭제된 후보자 멤버 ID"),
                fieldWithPath("data.electionId").description("후보자가 속해있던 선거 ID"),
                fieldWithPath("data.memberJobId").description("후보자가 속해있던 직위 ID")
            )));
  }

  @Test
  @DisplayName("[FAIL] 선거 후보자 단일 삭제 - 존재하지 않는 선거 후보자 ID")
  public void deleteCandidateFailByNotExist() throws Exception {
    mockMvc.perform(delete("/v1/admin/elections/candidate/{id}", -1)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14001))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 후보자입니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 선거 투표자 단일 등록")
  public void registerVoter() throws Exception {
    ElectionEntity election = generateElection(admin, true);

    mockMvc.perform(post("/v1/admin/elections/{eid}/voters/{vid}", election.getId(), user.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("election-register-voter",
            pathParameters(
                parameterWithName("eid").description("투표자를 등록할 선거 ID"),
                parameterWithName("vid").description("투표자로 등록할 멤버 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.electionId").description("투표자가 등록된 선거 ID"),
                fieldWithPath("data.voterId").description("등록된 투표자 멤버 ID")
            )));
  }

  @Test
  @DisplayName("[FAIL] 선거 투표자 단일 등록 - 이미 존재하는 투표자")
  public void registerVoterFailByExist() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    generateElectionVoter(user, election, false);

    mockMvc.perform(post("/v1/admin/elections/{eid}/voters/{vid}", election.getId(), user.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14004))
        .andExpect(jsonPath("$.msg").value("이미 등록된 투표자입니다."));
  }

  @Test
  @DisplayName("[FAIL] 선거 투표자 단일 등록 - 존재하지 않는 선거")
  public void registerVoterFailByNoneElection() throws Exception {
    Long id = -1L;

    mockMvc.perform(post("/v1/admin/elections/{eid}/voters/{vid}", id, user.getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 선거입니다."));
  }

  @Test
  @DisplayName("[FAIL] 선거 투표자 단일 등록 - 존재하지 않는 투표자")
  public void registerVoterFailByNoneVoter() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    Long id = -1L;

    mockMvc.perform(post("/v1/admin/elections/{eid}/voters/{vid}", election.getId(), id)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 회원입니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 선거 투표자 단일 삭제")
  public void deleteVoter() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    ElectionVoterEntity electionVoterInfo = generateElectionVoter(user, election, false);

    mockMvc.perform(delete("/v1/admin/elections/{eid}/voters/{vid}", election.getId(),
            electionVoterInfo.getElectionVoterPK().getVoter().getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data").value(user.getId()))
        .andDo(document("election-delete-voter",
            pathParameters(
                parameterWithName("eid").description("삭제할 투표자가 속한 선거 ID"),
                parameterWithName("vid").description("삭제할 투표자 멤버 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("삭제된 투표자 멤버 ID")
            )));

  }

  @Test
  @DisplayName("[FAIL] 선거 투표자 단일 삭제 - 권한 부족")
  public void deleteVoterFailByAuth() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    ElectionVoterEntity electionVoterInfo = generateElectionVoter(user, election, false);

    mockMvc.perform(delete("/v1/admin/elections/{eid}/voters/{vid}", election.getId(),
            electionVoterInfo.getElectionVoterPK().getVoter().getId())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1003))
        .andExpect(jsonPath("$.msg").value("접근이 거부되었습니다."));
  }

  @Test
  @DisplayName("[FAIL] 선거 투표자 단일 삭제 - 존재하지 않는 투표자")
  public void deleteVoterFailByVoter() throws Exception {
    ElectionEntity election = generateElection(admin, true);

    Long id = -1L;

    mockMvc.perform(delete("/v1/admin/elections/{eid}/voters/{vid}", election.getId(), id)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 회원입니다."));
  }

  @Test
  @DisplayName("[FAIL] 선거 투표자 단일 삭제 - 존재하지 않는 선거")
  public void deleteVoterFailByElection() throws Exception {
    ElectionEntity election = generateElection(admin, true);
    ElectionVoterEntity electionVoterInfo = generateElectionVoter(user, election, false);

    Long id = -1L;

    mockMvc.perform(delete("/v1/admin/elections/{eid}/voters/{vid}", id,
            electionVoterInfo.getElectionVoterPK().getVoter().getId())
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-14000))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 선거입니다."));
  }

  @Test
  @DisplayName("[FAIL] 선거 투표자 단일 삭제 - 문자열 파라미터")
  public void deleteVoterFailByString() throws Exception {
    ElectionEntity election = generateElection(admin, true);

    Long id = -1L;
    String param = "param";

    mockMvc.perform(delete("/v1/admin/elections/{eid}/voters/{vid}", id, param)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-9998))
        .andExpect(jsonPath("$.msg").value("파라미터 타입이 일치하지 않습니다."));
  }

}
