package keeper.project.homepage.user.controller.point;

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

import java.time.LocalDateTime;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class PointLogControllerTest extends ApiControllerTestHelper {

  private MemberEntity memberEntity1, memberEntity2, memberEntity3;
  private String userToken1, userToken2, userToken3;

  private final LocalDateTime now = LocalDateTime.now();

  int index = 0;

  @BeforeEach
  public void setUp() throws Exception {
    memberEntity1 = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    memberEntity2 = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    memberEntity3 = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    userToken1 = generateJWTToken(memberEntity1);
    userToken2 = generateJWTToken(memberEntity2);
    userToken3 = generateJWTToken(memberEntity3);
    for(int i = 0; i < 38; i++) {
      generateTestPointLog();
      generateTestPointGiftLog();
    }

    for(int i = 0; i < 10; i++) {
      generateTestOtherPointLog();
      generateTestOtherPointGiftLog();
    }

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

  private void generateTestOtherPointLog() {
    index += 1;
    final long epochTime = System.nanoTime();
    final LocalDateTime now = LocalDateTime.now();
    PointLogEntity pointLogEntity = PointLogEntity.builder()
        .member(memberEntity3)
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
        .detail("선물" + epochTime)
        .time(now)
        .presentedMember(memberEntity2)
        .isSpent(1)
        .build();

    pointLogRepository.save(pointLogEntity);
  }

  private void generateTestOtherPointGiftLog() {
    index += 1;
    final long epochTime = System.nanoTime();
    final LocalDateTime now = LocalDateTime.now();
    PointLogEntity pointLogEntity = PointLogEntity.builder()
        .member(memberEntity2)
        .point(index)
        .detail("선물" + epochTime)
        .time(now)
        .presentedMember(memberEntity3)
        .isSpent(1)
        .build();

    pointLogRepository.save(pointLogEntity);
  }

  @Test
  @DisplayName("포인트 선물하기 - 성공")
  public void transferPointSuccess() throws Exception {
    String content = "{\n"
        + "    \"time\": \"" + now + "\",\n"
        + "    \"point\": \"" + 10 + "\",\n"
        + "    \"detail\": \"" + "포인트 선물" + "\",\n"
        + "    \"presentedId\": \"" + memberEntity2.getId() + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/points/present")
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
        + "    \"detail\": \"" + "포인트 선물" + "\",\n"
        + "    \"presentedId\": \"" + 1234 + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/points/present")
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
        + "    \"point\": \"" + 987654321 + "\",\n"
        + "    \"detail\": \"" + "선물 드릴께용" + "\",\n"
        + "    \"presentedId\": \"" + memberEntity2.getId() + "\"\n"
        + "}";

    mockMvc.perform(post("/v1/points/present")
            .header("Authorization", userToken1)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("잔여 포인트가 부족합니다."));
  }

  @Test
  @DisplayName("포인트 내역 조회 - 성공")
  public void findAllPointLogByMember() throws Exception {
    mockMvc.perform(get("/v1/points")
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

}
