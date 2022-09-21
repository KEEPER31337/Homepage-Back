package keeper.project.homepage.systemadmin.controller;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.부회장;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.서기;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.전산관리자;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.총무;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.출제자;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.휴면회원;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class SystemAdminControllerTest extends SystemAdminControllerTestHelper {

  private MemberEntity user;
  private MemberEntity systemAdmin;

  private String userToken;
  private String systemAdminToken;

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(회원, 정회원, 일반회원);
    userToken = generateJWTToken(user);
    systemAdmin = generateMemberEntity(전산관리자, 정회원, 우수회원);
    systemAdminToken = generateJWTToken(systemAdmin);
  }

  @Test
  @DisplayName("[SUCCESS] 수정 가능한 ROLE 목록 불러오기")
  public void getJobList() throws Exception {

    mockMvc.perform(get("/v1/admin/system-admin/jobs")
            .header("Authorization", systemAdminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list").isNotEmpty())
        .andDo(document("get-job-list",
            responseFields(
                generateJobDtoResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] (부회장) 직책 등록")
  public void assignJob() throws Exception {
    MemberEntity member = generateMemberEntity(회원, 정회원, 일반회원);
    MemberJobEntity subMasterRole = memberJobRepository.findByName(부회장.getJobName()).get();

    mockMvc.perform(post("/v1/admin/system-admin/members/{memberId}/jobs/{jobId}", member.getId(),
            subMasterRole.getId())
            .header("Authorization", systemAdminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data.memberId").value(member.getId()))
        .andExpect(jsonPath("$.data.generation").value(member.getGeneration()))
        .andExpect(jsonPath("$.data.hasJobs.size()").value(member.getMemberJobs().size()))
        .andExpect(jsonPath("$.data.type.name").value(member.getMemberType().getName()))
        .andDo(document("assign-job",
            pathParameters(
                parameterWithName("memberId").description("ROLE을 부여할 member의 ID"),
                parameterWithName("jobId").description("등록할 ROLE id")
            ),
            responseFields(
                generateMemberJobTypeResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] (부회장) 직책 삭제")
  public void deleteJob() throws Exception {
    MemberEntity member = generateMemberEntity(회원, 정회원, 일반회원);
    MemberJobEntity subMasterRole = memberJobRepository.findByName(부회장.getJobName()).get();
    member.addMemberJob(subMasterRole);

    mockMvc.perform(delete("/v1/admin/system-admin/members/{memberId}/jobs/{jobId}", member.getId(),
            subMasterRole.getId())
            .header("Authorization", systemAdminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data.memberId").value(member.getId()))
        .andExpect(jsonPath("$.data.generation").value(member.getGeneration()))
        .andExpect(jsonPath("$.data.hasJobs.size()").value(member.getMemberJobs().size()))
        .andExpect(jsonPath("$.data.type.name").value(member.getMemberType().getName()))
        .andDo(document("delete-job",
            pathParameters(
                parameterWithName("memberId").description("ROLE을 삭제할 member의 ID"),
                parameterWithName("jobId").description("회원에게서 삭제할 ROLE id")
            ),
            responseFields(
                generateMemberJobTypeResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 역할별 회원 목록 불러오기")
  public void getMemberListByJob() throws Exception {
    generateMemberEntity(부회장, 휴면회원, 일반회원);
    generateMemberEntity(부회장, 휴면회원, 일반회원);
    generateMemberEntity(부회장, 휴면회원, 일반회원);

    MemberJobEntity subMasterJob = memberJobRepository.findByName(부회장.getJobName()).get();

    mockMvc.perform(get("/v1/admin/system-admin/members/jobs/{jobId}", subMasterJob.getId())
            .header("Authorization", systemAdminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.size()").value(3))
        .andExpect(jsonPath("$.list[0].hasJobs[0].name").value(부회장.getJobName()))
        .andDo(document("get-member-list-by-role",
            pathParameters(
                parameterWithName("jobId").description("회원 목록을 불러올 ROLE Id")
            ),
            responseFields(
                generateMemberJobTypeResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 역할을 가진 모든 회원 목록 불러오기")
  public void getMemberListHasJob() throws Exception {
    generateMemberEntity(부회장, 휴면회원, 일반회원);
    generateMemberEntity(총무, 휴면회원, 일반회원);
    generateMemberEntity(서기, 휴면회원, 일반회원);
    generateMemberEntity(출제자, 휴면회원, 일반회원); // ROLE_회원, ROLE_출제자는 역할을 가진 회원 목록에 집계 안됨

    mockMvc.perform(get("/v1/admin/system-admin/jobs/members")
            .header("Authorization", systemAdminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.size()").value(4))
        .andDo(document("get-member-list-has-job",
            responseFields(
                generateMemberJobTypeResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }
}