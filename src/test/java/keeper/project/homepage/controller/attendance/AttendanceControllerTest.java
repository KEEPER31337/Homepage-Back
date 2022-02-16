package keeper.project.homepage.controller.attendance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class AttendanceControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";
  final private String birthday = "1998-01-01";
  final private String ipAddress1 = "127.0.0.1";
  final private String ipAddress2 = "127.0.0.2";

  private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private MemberEntity memberEntity1, memberEntity2;
  private String userToken1, userToken2;


  private final Date now = Timestamp.valueOf(LocalDateTime.now());
  private final Date oneDayAgo = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
  private final Date twoDaysAgo = Timestamp.valueOf(LocalDateTime.now().minusDays(2));
  private final Date threeDaysAgo = Timestamp.valueOf(LocalDateTime.now().minusDays(3));

  @BeforeEach
  public void setUp() throws Exception {

    memberEntity1 = generateTestMember();
    memberEntity2 = generateTestMember();
    userToken1 = generateTestMemberJWT(memberEntity1);
    userToken2 = generateTestMemberJWT(memberEntity2);

    generateNewAttendanceWithTime(threeDaysAgo, memberEntity1);
    generateNewAttendanceWithTime(twoDaysAgo, memberEntity1);
    generateNewAttendanceWithTime(oneDayAgo, memberEntity1);

    generateNewAttendanceWithTime(threeDaysAgo, memberEntity2);
  }

  @Test
  public void createAttendSuccess() throws Exception {
    AttendanceDto attendanceDto = AttendanceDto.builder()
        .greetings("hi")
        .ipAddress(ipAddress1)
        .build();
    String content = objectMapper.writeValueAsString(attendanceDto);

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.post("/v1/attend")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON).content(content));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("attend-create",
            requestFields(
                fieldWithPath("ipAddress").description("IP 주소"),
                fieldWithPath("greetings").description("인삿말").type(JsonFieldType.STRING).optional()
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다")
            )));

    List<AttendanceEntity> attendanceEntities = attendanceRepository.findAllByMember(
        memberEntity1);
    AttendanceEntity attendance = attendanceEntities.get(attendanceEntities.size() - 1);
    assertEquals(1, (int) attendance.getRank());
  }

  @Test
  @DisplayName("이미 출석한 상태라 출석 실패")
  public void createAttendFailedAlreadyAttend() throws Exception {
    generateNewAttendanceWithTime(now, memberEntity1);

    AttendanceDto attendanceDto = AttendanceDto.builder()
        .greetings("hi")
        .ipAddress(ipAddress1)
        .build();
    String content = objectMapper.writeValueAsString(attendanceDto);

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.post("/v1/attend")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON).content(content));

    result.andExpect(MockMvcResultMatchers.status().is5xxServerError())
        .andDo(print());
  }

  @Test
  @DisplayName("출석 업데이트 성공")
  public void updateAttendSuccess() throws Exception {
    generateNewAttendanceWithTime(now, memberEntity1);

    String newGreeting = "new 출석인삿말";
    AttendanceDto attendanceDto = AttendanceDto.builder()
        .greetings(newGreeting)
        .build();
    String newContent = objectMapper.writeValueAsString(attendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .patch("/v1/attend")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(newContent))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("attend-update",
            requestFields(
                fieldWithPath("greetings").description("인삿말").type(JsonFieldType.STRING).optional()
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다")
            )));
  }

  @Test
  @DisplayName("출석 업데이트 생성된 출석 없음 실패")
  public void updateAttendFailed() throws Exception {
    String attendanceFailedCode = messageSource.getMessage("attendanceFailed.code", null,
        LocaleContextHolder.getLocale());

    String newGreeting = "new 출석인삿말";
    AttendanceDto newAttendanceDto = AttendanceDto.builder()
        .greetings(newGreeting)
        .build();
    String newContent = objectMapper.writeValueAsString(newAttendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .patch("/v1/attend")
            .header("Authorization", userToken2)
            .contentType(MediaType.APPLICATION_JSON)
            .content(newContent))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(Long.valueOf(attendanceFailedCode)))
        .andExpect(jsonPath("$.msg").value("출석을 하지 않았습니다."))
        .andDo(print());
  }

  @Test
  @DisplayName("내 출석 날짜 불러오기 성공")
  public void getMyAttendDateListSuccess() throws Exception {

    LocalDate oneDayAgoParam = LocalDate.now().minusDays(1);
    LocalDate twoDaysAgoParam = LocalDate.now().minusDays(2);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/date")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON)
            .param("startDate", String.valueOf(twoDaysAgoParam))
            .param("endDate", String.valueOf(oneDayAgoParam)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.list.length()").value(1))
        .andDo(print())
        .andDo(document("attend-get-my-date-list",
            requestParameters(
                parameterWithName("startDate").description("시작 날짜(YYYY-MM-DD)").optional(),
                parameterWithName("endDate").description("종료 날짜(해당 날짜는 포함되지 않습니다)").optional()
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("list").description("입력한 기간 사이의 출석 날짜를 반환합니다")
            )));
  }

  @Test
  @DisplayName("내 출석 날짜 불러오기 실패(시작날짜 > 종료날짜)")
  public void getMyAttendDateListFailed() throws Exception {

    LocalDate nowParam = LocalDate.now();
    LocalDate twoDaysAgoParam = LocalDate.now().minusDays(2);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/date")
            .header("Authorization", userToken1)
            .param("startDate", String.valueOf(nowParam))
            .param("endDate", String.valueOf(twoDaysAgoParam)))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError())
        .andDo(print());
  }

  @Test
  @DisplayName("내 출석 정보 불러오기 성공")
  public void getMyAttendSuccess() throws Exception {

    LocalDate oneDayAgoParam = LocalDate.now().minusDays(1);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/info")
            .header("Authorization", userToken1)
            .param("date", String.valueOf(oneDayAgoParam)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andDo(print())
        .andDo(document("attend-get-info",
            requestParameters(
                parameterWithName("date").description("정보를 가져올 날짜(YYYY-MM-DD)")
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data.id").description("출석 id"),
                fieldWithPath("data.time").description("출석한 시간"),
                fieldWithPath("data.point").description("해당일 받은 순위권 + 개근 point"),
                fieldWithPath("data.randomPoint").description("해당일 받은 random Point"),
                fieldWithPath("data.ipAddress").description("출석 당시 ip 주소"),
                fieldWithPath("data.greetings").description("해당일 출석 메시지"),
                fieldWithPath("data.continuousDay").description("현재 개근 일 수"),
                fieldWithPath("data.rank").description("랭킹"),
                subsectionWithPath("data.member").description("회원 정보")
            )));

    LocalDate twoDaysAgoParam = LocalDate.now().minusDays(2);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/info")
            .header("Authorization", userToken1)
            .param("date", String.valueOf(twoDaysAgoParam)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andDo(print());
  }

  @Test
  @DisplayName("모든 출석 정보 불러오기 성공")
  public void getAllAttendSuccess() throws Exception {

    LocalDate threeDaysAgoParam = LocalDate.now().minusDays(3);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/all")
            .header("Authorization", userToken1)
            .param("date", String.valueOf(threeDaysAgoParam)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.list.length()").value(2))
        .andDo(print())
        .andDo(document("attend-get-all",
            requestParameters(
                parameterWithName("date").description("정보를 가져올 날짜(YYYY-MM-DD)")
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("list[].ipAddress").description("출석 당시 ip 주소(가려서)"),
                fieldWithPath("list[].nickName").description("사용자 이름"),
                subsectionWithPath("list[].thumbnail").description("썸네일 정보"),
                fieldWithPath("list[].greetings").description("해당일 출석 메시지"),
                fieldWithPath("list[].continuousDay").description("현재 개근 일 수"),
                fieldWithPath("list[].rank").description("랭킹")
            )));
  }

  @Test
  @DisplayName("보너스 포인트 정보 불러오기")
  public void getPointInfo() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/point-info"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("attend-get-point-info",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data.FIRST_PLACE_POINT").description("1등 추가 포인트"),
                fieldWithPath("data.SECOND_PLACE_POINT").description("2등 추가 포인트"),
                fieldWithPath("data.THIRD_PLACE_POINT").description("3등 추가 포인트"),
                fieldWithPath("data.WEEK_ATTENDANCE").description("주 개근 일 수"),
                fieldWithPath("data.MONTH_ATTENDANCE").description("월 개근 일 수"),
                fieldWithPath("data.YEAR_ATTENDANCE").description("연 개근 일 수"),
                fieldWithPath("data.DAILY_ATTENDANCE_POINT").description("기본 출석 포인트"),
                fieldWithPath("data.WEEK_ATTENDANCE_POINT").description("주 개근 추가 포인트"),
                fieldWithPath("data.MONTH_ATTENDANCE_POINT").description("월 개근 추가 포인트"),
                fieldWithPath("data.YEAR_ATTENDANCE_POINT").description("연 개근 추가 포인트")
            )));
  }

  private void generateNewAttendanceWithTime(Date time, MemberEntity memberEntity)
      throws Exception {
    Random random = new Random();
    attendanceRepository.save(
        AttendanceEntity.builder()
            .point(10)
            .continuousDay(0)
            .greetings("hi")
            .ipAddress(ipAddress1)
            .time(time)
            .member(memberEntity)
            .rank(3)
            .randomPoint((int) (Math.random() * 900 + 100))
            .build());
  }

  private String generateTestMemberJWT(MemberEntity member) throws Exception {

    String content = "{\n"
        + "    \"loginId\": \"" + member.getLoginId() + "\",\n"
        + "    \"password\": \"" + password + "\"\n"
        + "}";
    MvcResult result = mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    String resultString = result.getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    SingleResult<SignInDto> sign = mapper.readValue(resultString, new TypeReference<>() {
    });
    return sign.getData().getToken();
  }

  private MemberEntity generateTestMember() {
    final long epochTime = System.nanoTime();
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberJobEntity)
        .build();
    MemberEntity memberEntity = MemberEntity.builder()
        .loginId(loginId + epochTime)
        .password(passwordEncoder.encode(password))
        .realName(realName + epochTime)
        .nickName(nickName + epochTime)
        .emailAddress(emailAddress + epochTime)
        .studentId(studentId + epochTime)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);
    return memberEntity;
  }
}
