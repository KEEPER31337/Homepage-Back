package keeper.project.homepage.controller.attendance;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class GameControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";

  private MemberEntity memberEntity1;
  private String userToken1;

  @BeforeEach
  public void setUp() throws Exception {

    memberEntity1 = generateTestMember();
    userToken1 = generateTestMemberJWT(memberEntity1);
  }

  @Test
  @DisplayName("주사위 게임 횟수 출력")
  public void checkDiceInfo() throws Exception {

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/game/dice/info").header("Authorization", userToken1));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-diceInfo",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data").description("주사위 게임 한 횟수")
            )));
  }

  @Test
  @DisplayName("주사위 게임 실행")
  public void playDice() throws Exception {

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/game/dice/play").header("Authorization", userToken1)
            .param("bet", "1000"));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-dicePlay",
            requestParameters(
                parameterWithName("bet").description("베팅할 금액")
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다")
            )));
  }

  @Test
  @DisplayName("주사위 게임 결과 저장")
  public void saveResult() throws Exception {

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/game/dice/save").header("Authorization", userToken1)
            .param("bet", "1000")
            .param("result", "1"));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-diceSave",
            requestParameters(
                parameterWithName("bet").description("베팅한 금액"),
                parameterWithName("result").description("결과 (이기면 1, 비기면 0, 지면 -1)")
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다")
            )));
  }

  @Test
  @DisplayName("주사위 게임 횟수 제한")
  public void validateDice() throws Exception {

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/game/dice/check").header("Authorization", userToken1));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-diceValid",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data").description("횟수(6)가 넘어갔으면 true, 아니면 false")
            )));
  }

  @Test
  @DisplayName("룰렛 게임 실행")
  public void playRoulette() throws Exception {

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/game/roulette/play").header("Authorization", userToken1));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-roulettePlay",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data.roulettePerDay").description("하루 룰렛 게임 한 횟수"),
                fieldWithPath("data.roulettePoints").description(
                    "룰렛판에 들어갈 랜덤으로 생성된 포인트 배열 (length:8)"),
                fieldWithPath("data.roulettePointIdx").description(
                    "랜덤으로 하나 뽑은 포인트의 index (0~7중 랜덤 한 숫자)")
            )));
  }

  @Test
  @DisplayName("룰렛 게임 횟수 제한")
  public void validateRoulette() throws Exception {

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/game/roulette/check").header("Authorization", userToken1));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-rouletteValid",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data").description("횟수(3)가 넘어갔으면 true, 아니면 false")
            )));
  }

  @Test
  @DisplayName("로또 게임 횟수 출력")
  public void checkLottoInfo() throws Exception {

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/game/lotto/info").header("Authorization", userToken1));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-lottoInfo",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data").description("로또 게임 한 횟수")
            )));
  }

  @Test
  @DisplayName("로또 게임 실행")
  public void playLotto() throws Exception {

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/game/lotto/play").header("Authorization", userToken1));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-lottoPlay",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data").description("로또 게임 결과(등수)")
            )));
  }

  @Test
  @DisplayName("로또 게임 횟수 제한")
  public void validateLotto() throws Exception {

    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/game/lotto/check").header("Authorization", userToken1));

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-lottoValid",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data").description("횟수(1)가 넘어갔으면 true, 아니면 false")
            )));
  }

  @Test
  @DisplayName("게임 정보 불러오기")
  public void getGameInfo() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/game/info"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("game-getInfo",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("data.DICE_BET_MAX").description("주사위 게임 최대 베팅 금액"),
                fieldWithPath("data.ROULETTE_FEE").description("룰렛 참가 비용"),
                fieldWithPath("data.ROULETTE_LIST").description("룰렛 당첨액 뽑히는 리스트"),
                fieldWithPath("data.LOTTO_FEE").description("로또 참가 비용"),
                fieldWithPath("data.FIRST_PROB").description("1등 확률"),
                fieldWithPath("data.SECOND_PROB").description("2등 확률"),
                fieldWithPath("data.THIRD_PROB").description("3등 확률"),
                fieldWithPath("data.FOURTH_PROB").description("4등 확률"),
                fieldWithPath("data.FIFTH_PROB").description("5등 확률"),
                fieldWithPath("data.FIRST_POINT").description("1등 당첨액"),
                fieldWithPath("data.SECOND_POINT").description("2등 당첨액"),
                fieldWithPath("data.THIRD_POINT").description("3등 당첨액"),
                fieldWithPath("data.FOURTH_POINT").description("4등 당첨액"),
                fieldWithPath("data.FIFTH_POINT").description("5등 당첨액"),
                fieldWithPath("data.LAST_POINT").description("6등 당첨액")
            )));
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
