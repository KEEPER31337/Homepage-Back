package keeper.project.homepage.controller.point;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class PointLogControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";
  final private Integer point = 200;

  private MemberEntity memberEntity1, memberEntity2;
  private String userToken1, userToken2;

  private final LocalDateTime now = LocalDateTime.now();

  int index = 0;

  @BeforeEach
  public void setUp() throws Exception {

    memberEntity1 = generateTestMember();
    memberEntity2 = generateTestMember();
    userToken1 = generateTestMemberJWT(memberEntity1);
    userToken2 = generateTestMemberJWT(memberEntity2);
    for (int i = 0; i < 38; i++) {
      generateTestPointLog();
      generateTestPointGiftLog();
    }

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
        .point(point)
        .generation(0F)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);
    return memberEntity;
  }

  private void generateTestPointLog() {
    index += 1;
    final long epochTime = System.nanoTime();
    final LocalDateTime now = LocalDateTime.now();
    PointLogEntity pointLogEntity = PointLogEntity.builder()
        .member(memberEntity1)
        .point(index)
        .detail("적립" + epochTime)
        .time(now)
        .isSpent(0)
        .build();

    pointLogRepository.save(pointLogEntity);
  }

  private void generateTestPointGiftLog() {
    index += 1;
    final long epochTime = System.nanoTime();
    final LocalDateTime now = LocalDateTime.now();
    PointLogEntity pointLogEntity = PointLogEntity.builder()
        .member(memberEntity1)
        .point(index)
        .detail("보낸 선물" + epochTime)
        .time(now)
        .presentedMember(memberEntity2)
        .isSpent(1)
        .build();

    pointLogRepository.save(pointLogEntity);
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

  @Test
  @DisplayName("포인트 선물하기 - 성공")
  public void transferPointSuccess() throws Exception {
    String content = "{\n"
        + "    \"time\": \"" + now + "\",\n"
        + "    \"point\": \"" + 10 + "\",\n"
        + "    \"detail\": \"" + "선물 드릴께용" + "\",\n"
        + "    \"presentedId\": \"" + memberEntity2.getId() + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/point/transfer")
            .header("Authorization", userToken1)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("point-transfer",
            requestFields(
                fieldWithPath("time").description("선물한 포인트 로그가 생성되는 시간"),
                fieldWithPath("point").description("선물한 포인트 값"),
                fieldWithPath("detail").description("선물 시 작성하게 되는 내용"),
                fieldWithPath("presentedId").description("선물 받는 멤버의 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.memberName").description("포인트를 선물한 멤버의 실제 이름"),
                fieldWithPath("data.time").description("선물한 포인트 로그가 생성된 시간"),
                fieldWithPath("data.point").description("선물한 포인트 값"),
                fieldWithPath("data.detail").description("선물 시 작성한 내용"),
                fieldWithPath("data.presentedMemberName").description("포인트를 선물받은 멤버의 실제 이름"),
                fieldWithPath("data.prePointMember").description("포인트를 선물한 멤버의 선물 보내기 전 포인트"),
                fieldWithPath("data.finalPointMember").description("포인트를 선물한 멤버의 선물 보낸 후 포인트"),
                fieldWithPath("data.prePointPresented").description("포인트를 선물받은 멤버의 선물 받기 전 포인트"),
                fieldWithPath("data.finalPointPresented").description("포인트를 선물받은 멤버의 선물 받은 후 포인트")
            )));
  }

  @Test
  @DisplayName("포인트 선물하기 - 실패(존재하지 않는 멤버에게 전송)")
  public void transferPointFailByNullMember() throws Exception {
    String content = "{\n"
        + "    \"time\": \"" + now + "\",\n"
        + "    \"point\": \"" + 10 + "\",\n"
        + "    \"detail\": \"" + "선물 드릴께용" + "\",\n"
        + "    \"presentedId\": \"" + 1234 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/point/transfer")
            .header("Authorization", userToken1)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 회원입니다."));
  }

  @Test
  @DisplayName("포인트 선물하기 - 실패(보내는 멤버의 포인트 부족)")
  public void transferPointFailByLackPoint() throws Exception {
    String content = "{\n"
        + "    \"time\": \"" + now + "\",\n"
        + "    \"point\": \"" + 1000 + "\",\n"
        + "    \"detail\": \"" + "선물 드릴께용" + "\",\n"
        + "    \"presentedId\": \"" + 1234 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/point/transfer")
            .header("Authorization", userToken1)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("잔여 포인트가 부족합니다."));
  }

  @Test
  @DisplayName("포인트 내역 조회 - 성공")
  public void findAllPointLogByMember() throws Exception {
    mockMvc.perform(get("/v1/point/lists/log")
            .param("page", "0")
            .param("size", "20")
            .header("Authorization", userToken1))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("point-lists-log",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 20)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].memberId").description("해당 포인트 로그를 보유한 멤버의 ID"),
                fieldWithPath("list[].time").description("해당 포인트 로그가 생성된 시간"),
                fieldWithPath("list[].point").description("포인트 변화량"),
                fieldWithPath("list[].detail").description("포인트 변화를 발생시킨 내용"),
                fieldWithPath("list[].isSpent").description("포인트 사용/적립 여부(0: 적립, 1: 사용)"),
                fieldWithPath("list[].prePoint").description("포인트 변화가 발생하기 전 멤버의 포인트"),
                fieldWithPath("list[].finalPoint").description("포인트 변화 발생 후 멤버의 포인트")
            )));
  }

  @Test
  @DisplayName("선물한 포인트 내역 조회 - 성공")
  public void findAllSentPointGiftLog() throws Exception {
    mockMvc.perform(get("/v1/point/lists/gift/sent")
            .param("page", "0")
            .param("size", "20")
            .header("Authorization", userToken1))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("point-lists-sentLog",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 20)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].memberName").description("포인트를 선물한 멤버의 실제 이름"),
                fieldWithPath("list[].time").description("포인트 선물 로그가 생성된 시간"),
                fieldWithPath("list[].point").description("선물한 포인트"),
                fieldWithPath("list[].detail").description("선물 시 작성한 내용"),
                fieldWithPath("list[].presentedMemberName").description("포인트를 선물받은 멤버의 실제 이름"),
                fieldWithPath("list[].prePointMember").description("포인트를 선물한 멤버의 선물하기 전 포인트"),
                fieldWithPath("list[].finalPointMember").description("포인트를 선물한 멤버의 선물한 후 포인트"),
                fieldWithPath("list[].prePointPresented").description("포인트를 선물받은 멤버의 선물받기 전 포인트"),
                fieldWithPath("list[].finalPointPresented").description("포인트를 선물받은 멤버의 선물받은 후 포인트")
            )));
  }

  @Test
  @DisplayName("선물받은 포인트 내역 조회 - 성공")
  public void findAllReceivedPointGiftLog() throws Exception {
    mockMvc.perform(get("/v1/point/lists/gift/received")
            .param("page", "0")
            .param("size", "20")
            .header("Authorization", userToken2))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("point-lists-receivedLog",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 20)")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].memberName").description("포인트를 선물한 멤버의 실제 이름"),
                fieldWithPath("list[].time").description("포인트 선물 로그가 생성된 시간"),
                fieldWithPath("list[].point").description("선물한 포인트"),
                fieldWithPath("list[].detail").description("선물 시 작성한 내용"),
                fieldWithPath("list[].presentedMemberName").description("포인트를 선물받은 멤버의 실제 이름"),
                fieldWithPath("list[].prePointMember").description("포인트를 선물한 멤버의 선물하기 전 포인트"),
                fieldWithPath("list[].finalPointMember").description("포인트를 선물한 멤버의 선물한 후 포인트"),
                fieldWithPath("list[].prePointPresented").description("포인트를 선물받은 멤버의 선물받기 전 포인트"),
                fieldWithPath("list[].finalPointPresented").description("포인트를 선물받은 멤버의 선물받은 후 포인트")
            )));
  }

}
