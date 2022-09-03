package keeper.project.homepage.admin.controller.clerk;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.서기;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.PERSONAL;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendancesRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.SeminarCreateRequestDto;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
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

    mockMvc.perform(get("/v1/admin/clerk/seminars")
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("get-seminar-list",
            responseFields(
                generateSeminarDtoResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false",
                    "성공 시 0을 반환",
                    "성공: 성공하였습니다 +\n실패: 에러 메세지 반환")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 생성하기")
  public void createSeminar() throws Exception {
    SeminarCreateRequestDto requestDto = SeminarCreateRequestDto.builder()
        .openTime(LocalDateTime.now().plusWeeks(1L).withNano(0)).build();

    mockMvc.perform(post("/v1/admin/clerk/seminars")
            .header("Authorization", clerkToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(requestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("create-seminar",
            requestFields(fieldWithPath("openTime").description("생성할 세미나의 오픈 시간")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("세미나 id")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 삭제하기")
  public void deleteSeminar() throws Exception {
    SeminarEntity seminar = generateSeminar(LocalDateTime.now().withNano(0));

    mockMvc.perform(delete("/v1/admin/clerk/seminars/{seminarId}", seminar.getId())
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data").value(seminar.getId()))
        .andDo(document("delete-seminar",
            pathParameters(parameterWithName("seminarId").description("삭제할 세미나의 id")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("삭제한 세미나 id")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 출석 상태 목록 불러오기")
  public void getSeminarAttendanceStatuses() throws Exception {
    mockMvc.perform(get("/v1/admin/clerk/seminars/statuses")
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
  @DisplayName("[SUCCESS] 기간별 세미나 출석 목록 불러오기")
  public void getAllSeminarAttendances() throws Exception {
    SeminarAttendancesRequestDto seminarAttendancesRequestDto = SeminarAttendancesRequestDto.builder()
        .seasonStartDate(LocalDateTime.now())
        .seasonEndDate(LocalDateTime.now().plusWeeks(2))
        .build();
    SeminarEntity seminar = generateSeminar(LocalDateTime.now().plusWeeks(1));
    MemberEntity member1 = generateMember("이정학", 12.5F);
    MemberEntity member2 = generateMember("최우창", 12.5F);
    MemberEntity member3 = generateMember("정현모", 8F);
    MemberEntity member4 = generateMember("손현경", 13F);
    SeminarAttendanceEntity attendance = generateSeminarAttendance(member1, seminar,
        seminarAttendanceStatusRepository.getById(ATTENDANCE.getId()));
    SeminarAttendanceEntity lateness = generateSeminarAttendance(member3, seminar,
        seminarAttendanceStatusRepository.getById(LATENESS.getId()));
    SeminarAttendanceEntity absence = generateSeminarAttendance(member2, seminar,
        seminarAttendanceStatusRepository.getById(ABSENCE.getId()));
    SeminarAttendanceEntity personal = generateSeminarAttendance(member4, seminar,
        seminarAttendanceStatusRepository.getById(PERSONAL.getId()));
    SeminarAttendanceExcuseEntity seminarAttendanceExcuseEntity = generateSeminarAttendanceExcuse(
        personal);
    personal.setSeminarAttendanceExcuseEntity(seminarAttendanceExcuseEntity);

    seminar.getSeminarAttendances().add(attendance);
    seminar.getSeminarAttendances().add(lateness);
    seminar.getSeminarAttendances().add(absence);
    seminar.getSeminarAttendances().add(personal);

    mockMvc.perform(get("/v1/admin/clerk/seminars/attendances")
            .header("Authorization", clerkToken)
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(seminarAttendancesRequestDto)))
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
            requestFields(
                fieldWithPath("seasonStartDate").description("학기 시작 날짜(시간 포함)"),
                fieldWithPath("seasonEndDate").description("학기 종료 날짜(시간 포함)")
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
    SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.getById(
        ATTENDANCE.getId());
    SeminarAttendanceEntity attendanceEntity = generateSeminarAttendance(member, seminar,
        attendance);

    SeminarAttendanceUpdateRequestDto requestDto = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(4L)
        .absenceExcuse("예비군 훈련으로 인한 결석")
        .build();

    mockMvc.perform(
            patch("/v1/admin/clerk/seminars/attendances/{attendanceId}",
                attendanceEntity.getId())
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
                parameterWithName("attendanceId").description("세미나 출석 id")),
            requestFields(
                fieldWithPath("seminarAttendanceStatusId").description("세미나 id"),
                fieldWithPath("absenceExcuse").optional().description("개인사정 결석 사유")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.seminarAttendanceStatusType").description("수정 후 세미나 출석 상태")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 출석 목록 조회")
  public void getSeminarAttendances() throws Exception {
    SeminarEntity seminar = generateSeminar(LocalDateTime.now().plusWeeks(1));
    MemberEntity member1 = generateMember("이정학", 12.5F);
    MemberEntity member2 = generateMember("최우창", 12.5F);
    MemberEntity member3 = generateMember("정현모", 8F);

    SeminarAttendanceEntity attendanceEntity1 = generateSeminarAttendance(member1, seminar,
        seminarAttendanceStatusRepository.getById(ATTENDANCE.getId()));
    SeminarAttendanceEntity attendanceEntity2 = generateSeminarAttendance(member2, seminar,
        seminarAttendanceStatusRepository.getById(LATENESS.getId()));
    SeminarAttendanceEntity attendanceEntity3 = generateSeminarAttendance(member3, seminar,
        seminarAttendanceStatusRepository.getById(ABSENCE.getId()));

    seminar.getSeminarAttendances().add(attendanceEntity1);
    seminar.getSeminarAttendances().add(attendanceEntity2);
    seminar.getSeminarAttendances().add(attendanceEntity3);

    mockMvc.perform(
            get("/v1/admin/clerk/seminars/{seminarId}/attendances",
                seminar.getId())
                .header("Authorization", clerkToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.size()").value(3))
        .andDo(document("get-seminar-attendance",
            pathParameters(
                parameterWithName("seminarId").description("세미나 id")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[].attendanceId").description("세미나 출석 Id"),
                fieldWithPath("list.[].memberName").description("해당 출석의 회원 이름")
            )));
  }
}
