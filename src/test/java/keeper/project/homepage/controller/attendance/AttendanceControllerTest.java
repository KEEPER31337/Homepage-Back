package keeper.project.homepage.controller.attendance;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
@Log4j2
public class AttendanceControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";
  final private String ipAddress1 = "127.0.0.1";
  final private String ipAddress2 = "127.0.0.2";

  private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private MemberEntity memberEntity;

  @BeforeEach
  public void setUp() throws Exception {

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
}
