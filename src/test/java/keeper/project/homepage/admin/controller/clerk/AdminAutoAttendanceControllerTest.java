package keeper.project.homepage.admin.controller.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import keeper.project.homepage.admin.dto.clerk.request.AttendanceConditionRequestDto;
import keeper.project.homepage.controller.clerk.AutoAttendanceSpringTestHelper;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
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
public class AdminAutoAttendanceControllerTest extends AutoAttendanceSpringTestHelper {

  private MemberEntity user;
  private MemberEntity admin;
  private String userToken;
  private String adminToken;

  @BeforeEach
  public void setUp() throws Exception {
    admin = generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
    adminToken = generateJWTToken(admin);
    user = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken = generateJWTToken(user);
  }

  @Test
  @DisplayName("[SUCCESS] 가장 최근의 세미나 조회")
  public void getLatestAttendanceId() throws Exception {
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .build());

    mockMvc.perform(get("/v1/admin/clerk/attendance")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.seminarId").value(seminar.getId()))
        .andExpect(jsonPath("$.data.seminarName").value(seminar.getName()))
        .andDo(document("seminar-admin-latest",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.seminarId").description("가장 최근의 세미나 ID"),
                fieldWithPath("data.seminarName").description("가장 최근의 세미나 이름")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 출석 조건 조회")
  public void getAttendanceConditions() throws Exception {
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .build());

    generateSeminarAttendance(seminar, admin);

    AttendanceConditionRequestDto attendanceConditionRequestDto = AttendanceConditionRequestDto.builder()
        .memberId(admin.getId())
        .seminarId(seminar.getId())
        .attendanceCloseTime(LocalDateTime.now().plusMinutes(10))
        .latenessCloseTime(LocalDateTime.now().plusMinutes(20))
        .build();

    mockMvc.perform(get("/v1/admin/clerk/attendance/conditions")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(attendanceConditionRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("seminar-attendance-conditions",
            requestFields(
                fieldWithPath("memberId").description("출석 조건을 조회하는 회장 ID"),
                fieldWithPath("seminarId").description("출석 조건을 조회할 세미나 ID"),
                fieldWithPath("attendanceCloseTime").description("세미나의 출석 인정시간").optional(),
                fieldWithPath("latenessCloseTime").description("세미나의 지각 인정 시간").optional()
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.attendanceCode").description("세미나의 출석 코드"),
                fieldWithPath("data.attendanceCloseTime").description("세미나의 출석 인정시간"),
                fieldWithPath("data.latenessCloseTime").description("세미나의 지각 인정 시간")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 출석이 종료된 후 무응답 회원 결석 처리")
  public void checkAttendanceEnd() throws Exception {
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .build());

    generateSeminarAttendance(seminar, admin);
    generateSeminarAttendance(seminar, user);

    mockMvc.perform(get("/v1/admin/clerk/attendance/end")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("seminar-no-reply-process-absence",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("무응답으로 결석 처리된 회원 수")
            )));
  }

}
