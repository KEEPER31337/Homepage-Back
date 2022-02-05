package keeper.project.homepage.controller.attendance;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
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
    generateNewAttendanceWithTime(now, memberEntity1);

    generateNewAttendanceWithTime(threeDaysAgo, memberEntity2);
  }

  @Test
  public void createAttend() throws Exception {
    AttendanceDto attendanceDto = AttendanceDto.builder().greetings("hi").ipAddress(ipAddress1)
        .memberId(memberEntity1.getId())
        .time(LocalDateTime.now()).build();
    String content = objectMapper.writeValueAsString(attendanceDto);

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.post("/v1/attend/check").contentType(
            MediaType.APPLICATION_JSON).content(content));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("attend-create",
            requestFields(
                fieldWithPath("time").description("출석 시간"),
                fieldWithPath("memberId").description("멤버 ID"),
                fieldWithPath("ipAddress").description("IP 주소"),
                fieldWithPath("greetings").optional().description("인삿말")
            )));
  }

  @Test
  @DisplayName("출석 업데이트 성공")
  public void updateAttendSuccess() throws Exception {

    String newGreeting = "new 출석인삿말";
    AttendanceDto attendanceDto = AttendanceDto.builder()
        .greetings(newGreeting)
        .build();
    String newContent = objectMapper.writeValueAsString(attendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .patch("/v1/attend/")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(newContent))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("attend-update",
            requestFields(
                fieldWithPath("greetings").description("출석 메시지")
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
            .patch("/v1/attend/")
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

    LocalDate nowParam = LocalDate.now();
    LocalDate twoDaysAgoParam = LocalDate.now().minusDays(2);
    AttendanceDto attendanceDto = AttendanceDto.builder()
        .startDate(twoDaysAgoParam)
        .endDate(nowParam)
        .build();
    String content = objectMapper.writeValueAsString(attendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/date")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.list.length()").value(2))
        .andDo(print())
        .andDo(document("attend-get-my-date-list",
            requestFields(
                fieldWithPath("startDate").description("시작 날짜").optional(),
                fieldWithPath("endDate").description("종료 날짜(해당 날짜는 포함되지 않습니다)").optional()
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
    AttendanceDto attendanceDto = AttendanceDto.builder()
        .startDate(nowParam)
        .endDate(twoDaysAgoParam)
        .build();
    String content = objectMapper.writeValueAsString(attendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/date")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError())
        .andDo(print());
  }

  @Test
  @DisplayName("내 출석 정보 불러오기 성공")
  public void getMyAttendSuccess() throws Exception {

    LocalDate nowParam = LocalDate.now();
    AttendanceDto attendanceDto = AttendanceDto.builder()
        .date(nowParam)
        .build();
    String content = objectMapper.writeValueAsString(attendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/info")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andDo(print())
        .andDo(document("attend-get-info",
            requestFields(
                fieldWithPath("date").description("정보를 가져올 날짜(YYYY-MM-DD)")
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
                fieldWithPath("data.continousDay").description("현재 개근 일 수"),
                subsectionWithPath("data.memberId").description("회원 정보")
            )));

    LocalDate twoDaysAgoParam = LocalDate.now().minusDays(2);
    AttendanceDto newAttendanceDto = AttendanceDto.builder()
        .date(twoDaysAgoParam)
        .build();
    String newContent = objectMapper.writeValueAsString(newAttendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/info")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(newContent))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.data").exists())
        .andDo(print());
  }

  @Test
  @DisplayName("모든 출석 정보 불러오기 성공")
  public void getAllAttendSuccess() throws Exception {

    LocalDate threeDaysAgoParam = LocalDate.now().minusDays(3);
    AttendanceDto attendanceDto = AttendanceDto.builder()
        .date(threeDaysAgoParam)
        .build();
    String content = objectMapper.writeValueAsString(attendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/attend/all")
            .header("Authorization", userToken1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.list.length()").value(2))
        .andDo(print())
        .andDo(document("attend-get-all",
            requestFields(
                fieldWithPath("date").description("정보를 가져올 날짜(YYYY-MM-DD)")
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("list[].id").description("출석 id"),
                fieldWithPath("list[].time").description("출석한 시간"),
                fieldWithPath("list[].point").description("해당일 받은 순위권 + 개근 point"),
                fieldWithPath("list[].randomPoint").description("해당일 받은 random Point"),
                fieldWithPath("list[].ipAddress").description("출석 당시 ip 주소"),
                fieldWithPath("list[].greetings").description("해당일 출석 메시지"),
                fieldWithPath("list[].continousDay").description("현재 개근 일 수"),
                subsectionWithPath("list[].memberId").description("회원 정보")
            )));
  }

  private void generateNewAttendanceWithTime(Date time, MemberEntity memberEntity)
      throws Exception {
    Random random = new Random();
    attendanceRepository.save(
        AttendanceEntity.builder()
            .point(10)
            .continousDay(0)
            .greetings("hi")
            .ipAddress(ipAddress1)
            .time(time)
            .memberId(memberEntity)
            .randomPoint(random.nextInt(100, 1001)).build());
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
    JacksonJsonParser jsonParser = new JacksonJsonParser();
    return jsonParser.parseMap(resultString).get("data").toString();
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
