package keeper.project.homepage.user.controller.clerk;

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
import keeper.project.homepage.controller.clerk.AutoAttendanceSpringTestHelper;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
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
public class AutoAttendanceControllerTest extends AutoAttendanceSpringTestHelper {

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
  @DisplayName("[SUCCESS] 가장 최근의 세미나 ID 조회")
  public void getLatestAttendanceId() throws Exception {
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .build());

    mockMvc.perform(get("/v1/clerk/attendance")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data").isNumber())
        .andDo(document("seminar-latest-id",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("가장 최근의 세미나 ID")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 출석 체크 - 출석 코드 불일치")
  public void inputInCorrectCode() throws Exception {
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .attendanceCode("379")
            .attendanceCloseTime(LocalDateTime.now().plusMinutes(5))
            .latenessCloseTime(LocalDateTime.now().plusMinutes(10))
            .build());

    generateSeminarAttendance(seminar, user);

    AttendanceCheckRequestDto attendanceCheckRequestDto = AttendanceCheckRequestDto.builder()
        .seminarId(seminar.getId())
        .memberId(user.getId())
        .attendanceCode("123")
        .attendTime(LocalDateTime.now().plusMinutes(15))
        .build();

    mockMvc.perform(get("/v1/clerk/attendance/check")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(attendanceCheckRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.isCorrect").value(false))
        .andDo(document("seminar-input-incorrect-code",
            requestFields(
                fieldWithPath("seminarId").description("세미나 ID"),
                fieldWithPath("memberId").description("출석 체크 하는 멤버 ID"),
                fieldWithPath("attendanceCode").description("출석 코드"),
                fieldWithPath("attendTime").description("출석 시간").optional()
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.isCorrect").description("출석 코드 일치 여부"),
                fieldWithPath("data.attendanceStatus").description("출석 상태"),
                fieldWithPath("data.totalDemerit").description("누적 벌점"),
                fieldWithPath("data.demerit").description("출석 체크로 인해 받은 벌점")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 출석 체크 - 출석/지각/결석")
  public void attendanceCheck() throws Exception {
    SeminarEntity seminar = seminarRepository.save(
        SeminarEntity.builder()
            .name("9월 1주차 세미나")
            .openTime(LocalDateTime.now())
            .attendanceCode("379")
            .attendanceCloseTime(LocalDateTime.now().plusMinutes(5))
            .latenessCloseTime(LocalDateTime.now().plusMinutes(10))
            .build());

    generateSeminarAttendance(seminar, user);

    AttendanceCheckRequestDto attendanceCheckRequestDto = AttendanceCheckRequestDto.builder()
        .seminarId(seminar.getId())
        .memberId(user.getId())
        .attendanceCode("379")
        .attendTime(LocalDateTime.now().plusMinutes(12))
        .build();

    mockMvc.perform(get("/v1/clerk/attendance/check")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(attendanceCheckRequestDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.isCorrect").value(true))
        .andDo(document("seminar-attendance-check",
            requestFields(
                fieldWithPath("seminarId").description("세미나 ID"),
                fieldWithPath("memberId").description("출석 체크 하는 멤버 ID"),
                fieldWithPath("attendanceCode").description("출석 코드"),
                fieldWithPath("attendTime").description("출석 시간").optional()
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.isCorrect").description("출석 코드 일치 여부"),
                fieldWithPath("data.attendanceStatus").description("출석 상태"),
                fieldWithPath("data.totalDemerit").description("누적 벌점"),
                fieldWithPath("data.demerit").description("출석 체크로 인해 받은 벌점")
            )));
  }
}
