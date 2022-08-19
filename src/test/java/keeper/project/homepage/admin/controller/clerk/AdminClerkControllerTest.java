package keeper.project.homepage.admin.controller.clerk;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.*;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.*;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import keeper.project.homepage.admin.dto.clerk.request.AssignJobRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.DeleteJobRequestDto;
import keeper.project.homepage.admin.dto.election.request.ElectionCreateRequestDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class AdminClerkControllerTest extends ClerkControllerTestHelper {

  private MemberEntity user;
  private MemberEntity clerk;

  private String userToken;
  private String clerkToken;

  @BeforeEach
  public void setUp() throws Exception {
    user = generateMemberEntity(회원, 정회원, 일반회원);
    userToken = generateJWTToken(user);
    clerk = generateMemberEntity(서기, 정회원, 우수회원);
    clerkToken = generateJWTToken(clerk);
  }

  @Test
  @DisplayName("[SUCCESS] 수정 가능한 ROLE 목록 불러오기")
  public void getJobList() throws Exception {

    mockMvc.perform(get("/v1/admin/clerk/jobs")
            .header("Authorization", clerkToken))
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
  @DisplayName("[SUCCESS] 수정 가능한 TYPE 목록 불러오기")
  public void getTypeList() throws Exception {

    mockMvc.perform(get("/v1/admin/clerk/types")
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list").isNotEmpty())
        .andDo(document("get-type-list",
            responseFields(
                generateTypeDtoResponseFields(ResponseType.LIST,
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

    AssignJobRequestDto assignJobRequestDto = new AssignJobRequestDto(subMasterRole.getId());

    mockMvc.perform(post("/v1/admin/clerk/jobs/{memberId}", member.getId())
            .header("Authorization", clerkToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(assignJobRequestDto)))
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
                parameterWithName("memberId").description("ROLE을 부여할 member의 ID")
            ),
            requestFields(
                fieldWithPath("jobId").description("등록할 ROLE id")
            ),
            responseFields(
                generateClerkMemberJobTypeResponseFields(ResponseType.SINGLE,
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
    assignJob(member, subMasterRole);

    DeleteJobRequestDto deleteJobRequestDto = new DeleteJobRequestDto(subMasterRole.getId());

    mockMvc.perform(delete("/v1/admin/clerk/jobs/{memberId}", member.getId())
            .header("Authorization", clerkToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(deleteJobRequestDto)))
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
                parameterWithName("memberId").description("ROLE을 삭제할 member의 ID")
            ),
            requestFields(
                fieldWithPath("jobId").description("회원에게서 삭제할 ROLE id")
            ),
            responseFields(
                generateClerkMemberJobTypeResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 활동 상태별 회원 목록 불러오기")
  public void getClerkMemberListByType() throws Exception {
    generateMemberEntity(회원, 휴면회원, 일반회원);
    generateMemberEntity(회원, 휴면회원, 일반회원);
    generateMemberEntity(회원, 휴면회원, 일반회원);
    generateMemberEntity(회원, 휴면회원, 일반회원);

    MemberTypeEntity dormantMemberType = memberTypeRepository.findByName(휴면회원.getTypeName()).get();

    mockMvc.perform(get("/v1/admin/clerk/members/types/{typeId}", dormantMemberType.getId())
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.size()").value(4))
        .andExpect(jsonPath("$.list[0].type.name").value(휴면회원.getTypeName()))
        .andDo(document("get-clerk-member-list-by-type",
            pathParameters(
                parameterWithName("typeId").description("회원 목록을 불러올 TYPE Id")
            ),
            responseFields(
                generateClerkMemberJobTypeResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 회원의 활동 상태 변경")
  public void updateMemberType() throws Exception {
    MemberEntity member = generateMemberEntity(회원, 정회원, 일반회원);

    MemberTypeEntity dormantMemberType = memberTypeRepository.findByName(휴면회원.getTypeName()).get();

    mockMvc.perform(put("/v1/admin/clerk/members/{memberId}/types/{typeId}",
            member.getId(), dormantMemberType.getId())
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data.memberId").value(member.getId()))
        .andExpect(jsonPath("$.data.type.name").value(휴면회원.getTypeName()))
        .andDo(document("update-member-type",
            pathParameters(
                parameterWithName("memberId").description("활동 상태를 변경할 회원의 Id"),
                parameterWithName("typeId").description("변경할 활동 TYPE id")
            ),
            responseFields(
                generateClerkMemberJobTypeResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }
}
