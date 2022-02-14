package keeper.project.homepage.controller.point;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
  @DisplayName("포인트 사용 로그 생성하기 - 성공")
  public void createPointUseLogSuccess() throws Exception {
    String content = "{\n"
        + "    \"time\": \"" + now + "\",\n"
        + "    \"point\": \"" + 100 + "\",\n"
        + "    \"detail\": \"" + "포인트복권에서 사용" + "\",\n"
        + "    \"isSpent\": \"" + 1 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/point/create")
            .header("Authorization", userToken1)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("포인트 적립 로그 생성하기 - 성공")
  public void createPointSaveLogSuccess() throws Exception {
    String content = "{\n"
        + "    \"time\": \"" + now + "\",\n"
        + "    \"point\": \"" + 1000 + "\",\n"
        + "    \"detail\": \"" + "포인트복권에서 적립" + "\",\n"
        + "    \"isSpent\": \"" + 0 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/point/create")
            .header("Authorization", userToken1)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @DisplayName("포인트 로그 생성하기 - 실패(잘못된 사용 여부 요청)")
  public void createPointSaveLogFail() throws Exception {
    String content = "{\n"
        + "    \"time\": \"" + now + "\",\n"
        + "    \"point\": \"" + 1000 + "\",\n"
        + "    \"detail\": \"" + "포인트복권에서 적립" + "\",\n"
        + "    \"isSpent\": \"" + 3 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/point/create")
            .header("Authorization", userToken1)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 포인트 사용 여부 요청입니다."));
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
        .andExpect(jsonPath("$.success").value(true));
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
        .andExpect(jsonPath("$.success").value(true));
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
        .andExpect(jsonPath("$.success").value(true));
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
        .andExpect(jsonPath("$.success").value(true));
  }

}
