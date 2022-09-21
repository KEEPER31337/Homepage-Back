package keeper.project.homepage.attendance.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Log4j2
public class RankControllerTest extends ApiControllerTestHelper {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";

  private MemberEntity memberEntity1, memberEntity2, memberEntity3;

  private String userToken;

  @BeforeEach
  public void setUp() throws Exception {

    memberEntity1 = generateTestMember(2000);
    memberEntity2 = generateTestMember(3000);
    memberEntity3 = generateTestMember(1000);
    userToken = generateJWTToken(memberEntity1.getLoginId(), password);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("포인트 순으로 랭킹 불러오기")
  public void getRankings() throws Exception {
    String prefix = ResponseType.PAGE.getReponseFieldPrefix();
    mockMvc.perform(get("/v1/rank")
            .header("Authorization", userToken)
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.page.content.size()").value(3))
        .andExpect(jsonPath("$.page.content[0].rank").value(1))
        .andExpect(jsonPath("$.page.content[1].rank").value(2))
        .andExpect(jsonPath("$.page.content[2].rank").value(3))
        .andDo(document("get-point-rankings",
            requestParameters(
                generateCommonPagingParameters("한 페이지당 출력 수(default = 10")
            ),
            responseFields(fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath(prefix + ".id").description("멤버 ID"),
                fieldWithPath(prefix + ".nickName").description("닉네임"),
                fieldWithPath(prefix + ".thumbnailPath").description("멤버 썸네일 이미지 조회 api path")
                    .optional(),
                fieldWithPath(prefix + ".jobs[]").description("멤버 직책"),
                fieldWithPath(prefix + ".point").description("포인트"),
                fieldWithPath(prefix + ".rank").description("포인트 등수"),
                fieldWithPath("page.empty").description("페이지가 비었는 지 여부"),
                fieldWithPath("page.first").description("첫 페이지 인지"),
                fieldWithPath("page.last").description("마지막 페이지 인지"),
                fieldWithPath("page.number").description("요소를 가져 온 페이지 번호 (0부터 시작)"),
                fieldWithPath("page.numberOfElements").description("요소 개수"),
                subsectionWithPath("page.pageable").description("해당 페이지에 대한 DB 정보"),
                fieldWithPath("page.size").description("요청한 페이지 크기"),
                subsectionWithPath("page.sort").description("정렬에 대한 정보"),
                fieldWithPath("page.totalElements").description("총 요소 개수"),
                fieldWithPath("page.totalPages").description("총 페이지")
            )));
  }

  private MemberEntity generateTestMember(int point) throws Exception {
    final long epochTime = System.nanoTime();
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberEntity memberEntity = MemberEntity.builder()
        .loginId(loginId + epochTime)
        .password(passwordEncoder.encode(password))
        .realName(realName + epochTime)
        .nickName(nickName + epochTime)
        .emailAddress(emailAddress + epochTime)
        .studentId(studentId + epochTime)
        .generation(0F)
        .point(point)
        .build();
    memberEntity.addMemberJob(memberJobEntity);
    memberRepository.save(memberEntity);
    return memberEntity;
  }
}