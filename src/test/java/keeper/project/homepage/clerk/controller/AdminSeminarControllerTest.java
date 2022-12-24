package keeper.project.homepage.clerk.controller;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.서기;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.BEFORE_ATTENDANCE;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.LATENESS;
import static keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity.SeminarAttendanceStatus.PERSONAL;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import keeper.project.homepage.clerk.dto.request.AttendanceStartRequestDto;
import keeper.project.homepage.clerk.dto.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.clerk.dto.request.SeminarCreateRequestDto;
import keeper.project.homepage.clerk.entity.SeminarAttendanceEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceExcuseEntity;
import keeper.project.homepage.clerk.entity.SeminarAttendanceStatusEntity;
import keeper.project.homepage.clerk.entity.SeminarEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    String seasonStartDate = String.valueOf(LocalDate.now());
    String seasonEndDate = String.valueOf(LocalDate.now().plusWeeks(2));

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
            .param("seasonStartDate", seasonStartDate)
            .param("seasonEndDate", seasonEndDate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("get-seminar-attendance-list",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 10)"),
                parameterWithName("seasonStartDate").description("학기 시작 날짜"),
                parameterWithName("seasonEndDate").description("학기 종료 날짜")
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
  @DisplayName("[SUCCESS] 특정 세미나 출석 목록 조회")
  public void getSeminarAttendances() throws Exception {
    SeminarEntity seminar = generateSeminar(LocalDateTime.now().plusWeeks(1));
    MemberEntity member1 = generateMember("이정학", 12.5F);
    MemberEntity member2 = generateMember("최우창", 12.5F);
    MemberEntity member3 = generateMember("정현모", 8F);
    MemberEntity member4 = generateMember("손현경", 13F);

    SeminarAttendanceEntity attendanceEntity1 = generateSeminarAttendance(member1, seminar,
        seminarAttendanceStatusRepository.getById(PERSONAL.getId()));
    attendanceEntity1.setSeminarAttendanceExcuseEntity(
        SeminarAttendanceExcuseEntity.builder()
            .seminarAttendanceEntity(attendanceEntity1)
            .absenceExcuse("예비군 훈련으로 인한 결석")
            .build());
    SeminarAttendanceEntity attendanceEntity2 = generateSeminarAttendance(member2, seminar,
        seminarAttendanceStatusRepository.getById(LATENESS.getId()));
    SeminarAttendanceEntity attendanceEntity3 = generateSeminarAttendance(member3, seminar,
        seminarAttendanceStatusRepository.getById(ABSENCE.getId()));
    SeminarAttendanceEntity attendanceEntity4 = generateSeminarAttendance(member4, seminar,
        seminarAttendanceStatusRepository.getById(ATTENDANCE.getId()));

    seminar.getSeminarAttendances().add(attendanceEntity1);
    seminar.getSeminarAttendances().add(attendanceEntity2);
    seminar.getSeminarAttendances().add(attendanceEntity3);
    seminar.getSeminarAttendances().add(attendanceEntity4);

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
        .andExpect(jsonPath("$.list.size()").value(4))
        .andDo(document("get-seminar-attendance",
            pathParameters(
                parameterWithName("seminarId").description("세미나 id")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[].attendanceId").description("세미나 출석 Id"),
                fieldWithPath("list.[].memberId").description("회원 id"),
                fieldWithPath("list.[].generation").description("회원 기수"),
                fieldWithPath("list.[].memberName").description("회원 이름"),
                fieldWithPath("list.[].attendanceStatusType").description("출석 상태"),
                fieldWithPath("list.[].absenceExcuse").optional().description("개인사정 결석 사유")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 해당 날짜의 세미나 조회")
  public void searchSeminarByDate() throws Exception {
    SeminarEntity seminar = generateSeminar(LocalDateTime.now());

    String searchDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    mockMvc.perform(get("/v1/admin/clerk/seminars/search")
            .header("Authorization", clerkToken)
            .param("searchDate", searchDate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.seminarId").value(seminar.getId()))
        .andExpect(jsonPath("$.data.seminarName").value(seminar.getName()))
        .andExpect(jsonPath("$.data.isExist").value(true))
        .andDo(document("get-seminar-by-date",
            requestParameters(
                parameterWithName("searchDate").description("해당 날짜 세미나 조회 날짜(yyyy-MM-dd)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.isExist").description(
                    "세미나가 존재하는 경우 : true +\n세미나가 존재하지 않는 경우 : false"
                ),
                fieldWithPath("data.seminarId").description(
                    "세미나가 존재하는 경우 : 조회된 세미나 ID +\n세미나가 존재하지 않는 경우 : -1"),
                fieldWithPath("data.seminarName").description(
                    "세미나가 존재하는 경우 : 조회된 세미나 이름 +\n세미나가 존재하지 않는 경우 : Not Exist Seminar"
                ),
                fieldWithPath("data.attendanceCloseTime").description(
                    "세미나가 존재하는 경우 : 출석 데이터가 있는 경우 출석 인정 시간, 없는 경우 null +\n세미나가 존재하지 않는 경우 : null"
                ),
                fieldWithPath("data.latenessCloseTime").description(
                    "세미나가 존재하는 경우 : 출석 데이터가 있는 경우 지각 인정 시간, 없는 경우 null +\n세미나가 존재하지 않는 경우 : null"
                ),
                fieldWithPath("data.attendanceCode").description(
                    "세미나가 존재하는 경우 : 출석 데이터가 있는 경우 출석 코드, 없는 경우 null+\n세미나가 존재하지 않는 경우 : null"
                )
            )));
  }

  @Test
  @DisplayName("[FAIL] 해당 날짜의 세미나 조회 - 세미나가 존재하지 않는 경우")
  public void searchSeminarByDateNotExist() throws Exception {
    String searchDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    mockMvc.perform(get("/v1/admin/clerk/seminars/search")
            .header("Authorization", clerkToken)
            .param("searchDate", searchDate))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.seminarId").value(-1))
        .andExpect(jsonPath("$.data.seminarName").value("Not Exist Seminar"))
        .andExpect(jsonPath("$.data.isExist").value(false));
  }

  @Test
  @DisplayName("[FAIL] 해당 날짜의 세미나 조회 - 날짜를 입력하지 않은 경우")
  public void searchSeminarByDateNoneDate() throws Exception {
    String searchDate = null;

    mockMvc.perform(get("/v1/admin/clerk/seminars/search")
            .header("Authorization", clerkToken)
            .param("searchDate", searchDate))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-9997))
        .andExpect(jsonPath("$.msg").value("요청 파라미터 입력이 필요합니다."));
  }

  @Test
  @DisplayName("[FAIL] 해당 날짜의 세미나 조회 - 형식에 맞지 않은 날짜 입력")
  public void searchSeminarByDateNotValidDate() throws Exception {
    String searchDate = "20200202";

    mockMvc.perform(get("/v1/admin/clerk/seminars/search")
            .header("Authorization", clerkToken)
            .param("searchDate", searchDate))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-9998))
        .andExpect(jsonPath("$.msg").value("파라미터 타입이 일치하지 않습니다."));
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 출석 시작")
  public void startSeminarAttendance() throws Exception {
    SeminarEntity seminar = generateSeminar(LocalDateTime.now());
    generateSeminarAttendance(clerk, seminar,
        seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId()));

    LocalDateTime attendanceCloseTime = LocalDateTime.now().plusSeconds(1);
    LocalDateTime latenessCloseTime = LocalDateTime.now().plusSeconds(2);

    AttendanceStartRequestDto request = new AttendanceStartRequestDto(
        seminar.getId(), attendanceCloseTime, latenessCloseTime);

    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(clerk.getId(), clerk.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_서기"))));

    mockMvc.perform(patch("/v1/admin/clerk/seminars/attendances/start")
            .header("Authorization", clerkToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("data.attendanceCode").value(seminar.getAttendanceCode()))
        .andDo(document("start-seminar-attendance",
            requestFields(
                fieldWithPath("seminarId").description("출석을 시작하려는 세미나 ID"),
                fieldWithPath("attendanceCloseTime").description(
                    "세미나 출석 인정 기준 시간(yyyy-MM-dd HH:mm:ss)"),
                fieldWithPath("latenessCloseTime").description(
                    "세미나 지각 인정 기준 시간(yyyy-MM-dd HH:mm:ss)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.attendanceCloseTime").description("세미나 출석 인정 기준 시간"),
                fieldWithPath("data.latenessCloseTime").description("세미나 지각 인정 기준 시간"),
                fieldWithPath("data.attendanceCode").description("세미나 출석 코드")
            )));
  }


  //* 1. 테스트 진행시 선행 작업 임의의 세미나 생성(ID는 9999L)
  //* 2. 임의의 세미나 출석 생성(세미나는 9999L, 멤버는 1L, 출석 상태 5L)
  //* 3. 해당 결과는 테스트 종료 후 DB 조회를 통해 확인 - 생성한 출석 상태가 5 -> 3번으로 변경
  //* 4. AdminSeminarService autoAttendanceAfterDeadline 수정
  //@Test
  @DisplayName("[SUCCESS] 세미나 출석 시작 - 자동 출석 확인")
  public void startSeminarAttendanceAutoAttendance() throws Exception {
    SeminarEntity seminar = seminarRepository.getById(9999L);
    generateSeminarAttendance(clerk, seminar,
        seminarAttendanceStatusRepository.getById(BEFORE_ATTENDANCE.getId()));

    LocalDateTime attendanceCloseTime = LocalDateTime.now().plusSeconds(1);
    LocalDateTime latenessCloseTime = LocalDateTime.now().plusSeconds(2);

    AttendanceStartRequestDto request = new AttendanceStartRequestDto(
        seminar.getId(), attendanceCloseTime, latenessCloseTime);

    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(clerk.getId(), clerk.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_서기"))));

    mockMvc.perform(patch("/v1/admin/clerk/seminars/attendances/start")
            .header("Authorization", clerkToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(request)))
        .andDo(print())
        .andExpect(status().isOk());

    Thread.sleep(5000);

  }

}
