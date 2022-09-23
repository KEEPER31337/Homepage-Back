package keeper.project.homepage.member.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import keeper.project.homepage.ApiControllerTestHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CommonMemberControllerTest extends ApiControllerTestHelper {


  @BeforeEach
  public void setUp() throws Exception {

    generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    generateMemberEntity(MemberJobName.회장, MemberTypeName.정회원, MemberRankName.우수회원);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }
  
  @Test
  @DisplayName("전체 회원 정보 불러오기")
  public void getAllCommonMembersInfo() throws Exception {
    String docMsg = "";
    String docCode = "";
    mockMvc.perform(get("/v1/common/members"))
        .andDo(print())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("common-members",
            responseFields(
                generateCommonMemberCommonResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", docCode, docMsg)
            )
        ));
  }
}
