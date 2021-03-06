package keeper.project.homepage.controller.attendance;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
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
  @DisplayName("????????? ????????? ?????? ????????????")
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
                generateCommonPagingParameters("??? ???????????? ?????? ???(default = 10")
            ),
            responseFields(fieldWithPath("success").description("?????? ????????? ????????? ?????? true"),
                fieldWithPath("code").description("?????? ????????? ????????? ?????? 0"),
                fieldWithPath("msg").description("?????? ????????? ????????? ?????? ?????????????????????"),
                fieldWithPath(prefix + ".id").description("?????? ID"),
                fieldWithPath(prefix + ".nickName").description("?????????"),
                fieldWithPath(prefix + ".thumbnailPath").description("?????? ????????? ????????? ?????? api path")
                    .optional(),
                fieldWithPath(prefix + ".jobs[]").description("?????? ??????"),
                fieldWithPath(prefix + ".point").description("?????????"),
                fieldWithPath(prefix + ".rank").description("????????? ??????"),
                fieldWithPath("page.empty").description("???????????? ????????? ??? ??????"),
                fieldWithPath("page.first").description("??? ????????? ??????"),
                fieldWithPath("page.last").description("????????? ????????? ??????"),
                fieldWithPath("page.number").description("????????? ?????? ??? ????????? ?????? (0?????? ??????)"),
                fieldWithPath("page.numberOfElements").description("?????? ??????"),
                subsectionWithPath("page.pageable").description("?????? ???????????? ?????? DB ??????"),
                fieldWithPath("page.size").description("????????? ????????? ??????"),
                subsectionWithPath("page.sort").description("????????? ?????? ??????"),
                fieldWithPath("page.totalElements").description("??? ?????? ??????"),
                fieldWithPath("page.totalPages").description("??? ?????????")
            )));
  }

  private MemberEntity generateTestMember(int point) throws Exception {
    final long epochTime = System.nanoTime();
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_??????").get();
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
        .generation(0F)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .point(point)
        .build();
    memberRepository.save(memberEntity);
    return memberEntity;
  }
}