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
import org.apache.tomcat.jni.Local;
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
  private MemberEntity memberEntity;
  private String userToken;

  @BeforeEach
  public void setUp() throws Exception {

    generateTestMember();
    userToken = generateTestMemberJWT();
  }

  @Test
  public void createAttend() throws Exception {
    AttendanceDto attendanceDto = AttendanceDto.builder().greetings("hi").ipAddress(ipAddress1)
        .memberId(memberEntity.getId())
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
    Date now = Timestamp.valueOf(LocalDateTime.now());
    generateNewAttendanceWithTime(now);

    String newGreeting = "new 출석인삿말";
    AttendanceDto newAttendanceDto = AttendanceDto.builder()
        .greetings(newGreeting)
        .build();
    String newContent = objectMapper.writeValueAsString(newAttendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .patch("/v1/attend/")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(newContent))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("attend-update",
            requestFields(
                fieldWithPath("greetings").description("출석 메시지"),
                subsectionWithPath("time").description("출석 시간"),
                subsectionWithPath("memberId").description("멤버 ID"),
                subsectionWithPath("ipAddress").description("IP 주소")
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

    Date twoDaysAgo = Timestamp.valueOf(LocalDateTime.now().minusDays(2));
    generateNewAttendanceWithTime(twoDaysAgo);

    String newGreeting = "new 출석인삿말";
    AttendanceDto newAttendanceDto = AttendanceDto.builder()
        .greetings(newGreeting)
        .build();
    String newContent = objectMapper.writeValueAsString(newAttendanceDto);
    mockMvc.perform(MockMvcRequestBuilders
            .patch("/v1/attend/")
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(newContent))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(Long.valueOf(attendanceFailedCode)))
        .andExpect(jsonPath("$.msg").value("출석을 하지 않았습니다."))
        .andDo(print());
  }

  private void generateNewAttendanceWithTime(Date time) throws Exception {
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

  private String generateTestMemberJWT() throws Exception {

    String content = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
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

  private void generateTestMember() {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberJobEntity)
        .build();
    memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);
  }
}
