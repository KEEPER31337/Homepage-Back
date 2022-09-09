package keeper.project.homepage.user.controller.clerk;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.BEFORE_ATTENDANCE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.clerk.request.AttendanceCheckRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class SeminarControllerTest extends ClerkControllerTestHelper {

  private MemberEntity member;
  private String memberToken;

  @BeforeEach
  public void setUp() throws Exception {
    member = generateMemberEntity(회원, 정회원, 우수회원);
    memberToken = generateJWTToken(member);
  }

  @Test
  @DisplayName("[SUCCESS] 출석 진행중인 세미나 조회")
  public void findSeminarOngoingAttendance() throws Exception {
    SeminarEntity seminar = generateSeminar(LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
        LocalDateTime.now().plusMinutes(10), "1234");

    String searchDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    mockMvc.perform(get("/v1/clerk/seminars/search/ongoing")
            .header("Authorization", memberToken)
            .param("searchDate", searchDate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.seminarId").value(seminar.getId()))
        .andExpect(jsonPath("$.data.seminarName").value(seminar.getName()))
        .andExpect(jsonPath("$.data.isExist").value(true))
        .andDo(document("get-seminar-ongoing-attendance",
            requestParameters(
                parameterWithName("searchDate").description("세미나 조회 날짜(yyyyMMdd)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.seminarId").description(
                    "세미나가 존재하는 경우 : 조회된 세미나 ID +\n세미나가 존재하지 않는 경우 : -1"),
                fieldWithPath("data.seminarName").description(
                    "세미나가 존재하는 경우 : 조회된 세미나 이름 +\n세미나가 존재하지 않는 경우 : Not Exist Seminar"
                ),
                fieldWithPath("data.isExist").description(
                    "세미나가 존재하는 경우 : true +\n세미나가 존재하지 않는 경우 : false"
                )
            )));
  }

  @Test
  @DisplayName("[FAIL] 출석 진행중인 세미나 조회 - 해당 날짜에 세미나가 없는 경우")
  public void findSeminarOngoingAttendanceFailByNoneSeminar() throws Exception {
    String searchDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    mockMvc.perform(get("/v1/clerk/seminars/search/ongoing")
            .header("Authorization", memberToken)
            .param("searchDate", searchDate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.seminarId").value(-1))
        .andExpect(jsonPath("$.data.seminarName").value("Not Exist Seminar"))
        .andExpect(jsonPath("$.data.isExist").value(false));
  }

  @Test
  @DisplayName("[FAIL] 출석 진행중인 세미나 조회 - 출석 체크를 진행하지 않은 세미나")
  public void findSeminarOngoingAttendanceFailByNotStartSeminar() throws Exception {
    generateSeminar(LocalDateTime.now(), null, null, null);

    String searchDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    mockMvc.perform(get("/v1/clerk/seminars/search/ongoing")
            .header("Authorization", memberToken)
            .param("searchDate", searchDate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.seminarId").value(-1))
        .andExpect(jsonPath("$.data.seminarName").value("Not Exist Seminar"))
        .andExpect(jsonPath("$.data.isExist").value(false));
  }

  @Test
  @DisplayName("[FAIL] 출석 진행중인 세미나 조회 - 출석 체크 시간이 지난 세미나")
  public void findSeminarOngoingAttendanceFailByAbsence() throws Exception {
    generateSeminar(LocalDateTime.now(), LocalDateTime.now().minusMinutes(10),
        LocalDateTime.now().minusMinutes(5), "1234");

    String searchDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    mockMvc.perform(get("/v1/clerk/seminars/search/ongoing")
            .header("Authorization", memberToken)
            .param("searchDate", searchDate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.seminarId").value(-1))
        .andExpect(jsonPath("$.data.seminarName").value("Not Exist Seminar"))
        .andExpect(jsonPath("$.data.isExist").value(false));
  }

  @Test
  @DisplayName("[SUCCESS] 출석 체크")
  public void checkSeminarAttendance() throws Exception {
    String code = "1234";
    SeminarEntity seminar = generateSeminar(LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
        LocalDateTime.now().plusMinutes(10), code);
    generateSeminarAttendance(member, seminar,
        seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId()));
    AttendanceCheckRequestDto request = new AttendanceCheckRequestDto(
        seminar.getId(), code);

    mockMvc.perform(post("/v1/clerk/seminars/attendances/check")
            .header("Authorization", memberToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.isPossibleAttendance").value(true))
        .andExpect(jsonPath("$.data.attendanceStatus").value("출석"))
        .andDo(document("check-seminar-attendance",
            requestFields(
              fieldWithPath("seminarId").description("출석을 진행할 세미나 ID"),
              fieldWithPath("attendanceCode").description("출석 코드")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.isPossibleAttendance").description(
                    "출석 가능 : true +\n출석 불가능(출석 시작전, 출석 종료) : false"
                ),
                fieldWithPath("data.attendanceStatus").description(
                    "출석 가능 : 출석 결과 +\n출석 불가능(출석 시작전, 출석 종료) : 출석 불가능"
                )
            )));
  }

  @Test
  @DisplayName("[FAIL] 출석 체크 - 존재하지 않는 세미나")
  public void checkSeminarAttendanceFailByNoneSeminar() throws Exception {
    String code = "1234";
    SeminarEntity seminar = generateSeminar(LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
        LocalDateTime.now().plusMinutes(10), code);
    generateSeminarAttendance(member, seminar,
        seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId()));
    AttendanceCheckRequestDto request = new AttendanceCheckRequestDto(
        -1L, code);

    mockMvc.perform(post("/v1/clerk/seminars/attendances/check")
            .header("Authorization", memberToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(request)))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  @DisplayName("[FAIL] 출석 체크 - 유효하지 않은 요청")
  public void checkSeminarAttendanceFailByNotValidRequest() throws Exception {
    String code = "1234";
    SeminarEntity seminar = generateSeminar(LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
        LocalDateTime.now().plusMinutes(10), code);
    generateSeminarAttendance(member, seminar,
        seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId()));
    AttendanceCheckRequestDto request = new AttendanceCheckRequestDto();

    mockMvc.perform(post("/v1/clerk/seminars/attendances/check")
            .header("Authorization", memberToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  @DisplayName("[FAIL] 출석 체크 - 출석을 시작하지 않은 세미나")
  public void checkSeminarAttendanceFailByNotStartAttendance() throws Exception {
    String code = "1234";
    SeminarEntity seminar = generateSeminar(LocalDateTime.now(), null, null, null);
    generateSeminarAttendance(member, seminar,
        seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId()));
    AttendanceCheckRequestDto request = new AttendanceCheckRequestDto(
        seminar.getId(), code);

    mockMvc.perform(post("/v1/clerk/seminars/attendances/check")
            .header("Authorization", memberToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.isPossibleAttendance").value(false))
        .andExpect(jsonPath("$.data.attendanceStatus").value("출석불가능"));
  }

  @Test
  @DisplayName("[FAIL] 출석 체크 - 종료된 출석")
  public void checkSeminarAttendanceFailByEndAttendance() throws Exception {
    String code = "1234";
    SeminarEntity seminar = generateSeminar(LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(10),
        LocalDateTime.now().minusMinutes(5), code);
    generateSeminarAttendance(member, seminar,
        seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId()));
    AttendanceCheckRequestDto request = new AttendanceCheckRequestDto(
        seminar.getId(), code);

    mockMvc.perform(post("/v1/clerk/seminars/attendances/check")
            .header("Authorization", memberToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.isPossibleAttendance").value(false))
        .andExpect(jsonPath("$.data.attendanceStatus").value("출석불가능"));
  }

  @Test
  @DisplayName("[FAIL] 출석 체크 - 출석 코드 불일치")
  public void checkSeminarAttendanceFailByCode() throws Exception {
    String code = "1234";
    SeminarEntity seminar = generateSeminar(LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
        LocalDateTime.now().plusMinutes(10), code);
    generateSeminarAttendance(member, seminar,
        seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId()));
    String userCode = "4321";
    AttendanceCheckRequestDto request = new AttendanceCheckRequestDto(
        seminar.getId(), userCode);

    mockMvc.perform(post("/v1/clerk/seminars/attendances/check")
            .header("Authorization", memberToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(request)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-15001))
        .andExpect(jsonPath("$.msg").value("출석 코드가 일치하지 않습니다."));
  }

}
