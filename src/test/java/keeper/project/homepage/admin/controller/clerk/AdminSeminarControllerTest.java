package keeper.project.homepage.admin.controller.clerk;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.서기;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.PERSONAL;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class AdminSeminarControllerTest extends ClerkControllerTestHelper {

  private MemberEntity clerk;
  private String clerkToken;


  @BeforeEach
  public void setUp() throws Exception {
    clerk = generateMemberEntity(서기, 정회원, 우수회원);
    clerkToken = generateJWTToken(clerk);
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 목록 불러오기")
  public void getSeminarList() throws Exception {
    generateSeminar(LocalDateTime.now().minusWeeks(1L));
    generateSeminar(LocalDateTime.now().plusWeeks(2L));
    generateSeminar(LocalDateTime.now().minusWeeks(2L));
    generateSeminar(LocalDateTime.now().plusWeeks(1L));
    mockMvc.perform(get("/v1/admin/clerk/seminar")
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list").isNotEmpty())
        .andDo(document("get-seminar-list",
            responseFields(
                generateSeminarDtoResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 출석 상태 목록 불러오기")
  public void getSeminarAttendanceStatuses() throws Exception {
    mockMvc.perform(get("/v1/admin/clerk/seminar/statuses")
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list").isNotEmpty())
        .andDo(document("get-seminar-attendance-statuses",
            responseFields(
                generateSeminarAttendanceStatusResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 모든 세미나의 출석 목록 불러오기")
  public void getAllSeminarAttendances() throws Exception {
    SeminarEntity seminar = generateSeminar(LocalDateTime.now().plusWeeks(1));
    MemberEntity member1 = generateMember("이정학", 12.5F);
    MemberEntity member2 = generateMember("최우창", 12.5F);
    MemberEntity member3 = generateMember("정현모", 9F);
    MemberEntity member4 = generateMember("손현경", 13F);
    SeminarAttendanceEntity attendance1 = generateSeminarAttendance(member1, seminar, ATTENDANCE);
    SeminarAttendanceEntity attendance2 = generateSeminarAttendance(member2, seminar, ABSENCE);
    SeminarAttendanceEntity attendance3 = generateSeminarAttendance(member3, seminar, LATENESS);
    SeminarAttendanceEntity personal = generateSeminarAttendance(member4, seminar, PERSONAL);
    SeminarAttendanceExcuseEntity seminarAttendanceExcuseEntity = generateSeminarAttendanceExcuse(
        personal);
    personal.setSeminarAttendanceExcuseEntity(seminarAttendanceExcuseEntity);

    seminar.getSeminarAttendanceEntity().add(attendance1);
    seminar.getSeminarAttendanceEntity().add(attendance2);
    seminar.getSeminarAttendanceEntity().add(attendance3);
    seminar.getSeminarAttendanceEntity().add(personal);

    mockMvc.perform(get("/v1/admin/clerk/seminar/attendance")
            .header("Authorization", clerkToken)
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("get-seminar-attendance-list",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                generateSeminarAttendanceResponseFields(ResponseType.PAGE,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 출석 정보 수정 - 개인사정 결석")
  public void updateSeminarAttendances() throws Exception {
    SeminarEntity seminar = generateSeminar(LocalDateTime.now().plusWeeks(1));
    MemberEntity member = generateMember("이정학", 12.5F);
    generateSeminarAttendance(member, seminar, ABSENCE);

    SeminarAttendanceUpdateRequestDto requestDto = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(4L)
        .absenceExcuse("예비군 훈련으로 인한 결석")
        .build();

    mockMvc.perform(
            patch("/v1/admin/clerk/seminar/attendance/{seminarId}/{memberId}",
                seminar.getId(),
                member.getId())
                .header("Authorization", clerkToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("update-seminar-attendance",
            pathParameters(
                parameterWithName("seminarId").description("세미나 id"),
                parameterWithName("memberId").description("출석을 수정할 회원 id")),
            requestFields(
                fieldWithPath("seminarAttendanceStatusId").description("세미나 id"),
                fieldWithPath("absenceExcuse").description("개인사정 결석 사유")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.seminarAttendanceStatusType").description("수정 후 세미나 출석 상태")
            )));
  }

}
