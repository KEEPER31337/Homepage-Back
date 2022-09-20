package keeper.project.homepage.controller.util;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AuthControllerTest extends ApiControllerTestHelper {

  private String userToken;

  MemberEntity memberEntity;

  @BeforeEach
  public void setUp() throws Exception {
    memberEntity = generateMemberEntity(MemberJobName.사서, MemberTypeName.정회원, MemberRankName.일반회원);
    MemberJobEntity memberJob = memberJobRepository.findByName(MemberJobName.전산관리자.getJobName())
        .get();
    MemberHasMemberJobEntity hasMemberJobEntity = memberHasMemberJobRepository.save(
        MemberHasMemberJobEntity.builder()
            .memberJobEntity(memberJob)
            .memberEntity(memberEntity)
            .build());
    memberJob.getMembers().add(hasMemberJobEntity);
    memberEntity.getMemberJobs().add(hasMemberJobEntity);
    userToken = generateJWTToken(memberEntity);
  }

  @Test
  @DisplayName("Jwt 토큰을 이용한 권한 확인")
  public void checkRolesTest() throws Exception {
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/v1/auth")
            .header("Authorization", userToken));

    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.list[0]").value("ROLE_사서"))
        .andExpect(jsonPath("$.list[1]").value("ROLE_전산관리자"))
        .andDo(print())
        .andDo(document("get-auth",
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("list[]").description("역할들이 담겨서 나갑니다.")
            )
        ));
  }
}