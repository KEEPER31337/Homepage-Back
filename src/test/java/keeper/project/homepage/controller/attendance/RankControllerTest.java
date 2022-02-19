package keeper.project.homepage.controller.attendance;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class RankControllerTest extends ApiControllerTestSetUp {

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "test@k33p3r.com";
  final private String studentId = "201724579";

  private MemberEntity memberEntity1, memberEntity2, memberEntity3;

  @BeforeEach
  public void setUp() throws Exception {

    memberEntity1 = generateTestMember(2000);
    memberEntity2 = generateTestMember(3000);
    memberEntity3 = generateTestMember(1000);
  }

  @Test
  @DisplayName("전체 랭킹 출력")
  public void showRanking() throws Exception {
    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/rank")
            .param("page", "0")
            .param("size", "2")
    );

    result.andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(print())
        .andDo(document("rank-list",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 25)")
            ),
            responseFields(
                fieldWithPath("success").description("에러 발생이 아니면 항상 true"),
                fieldWithPath("code").description("에러 발생이 아니면 항상 0"),
                fieldWithPath("msg").description("에러 발생이 아니면 항상 성공하였습니다"),
                fieldWithPath("list[].id").description("멤버 ID"),
                fieldWithPath("list[].emailAddress").description("이메일 주소"),
                fieldWithPath("list[].nickName").description("닉네임"),
                fieldWithPath("list[].birthday").description("생일"),
                fieldWithPath("list[].registerDate").description("회원가입 날짜"),
                subsectionWithPath("list[].memberType").description("멤버 타입").optional(), // 탈퇴회원 때문
                subsectionWithPath("list[].memberRank").description("멤버 랭크 타입").optional(),
                fieldWithPath("list[].point").description("포인트"),
                fieldWithPath("list[].level").description("레벨"),
                fieldWithPath("list[].thumbnail").description("멤버 썸네일"),
                fieldWithPath("list[].follower").description("팔로워"),
                fieldWithPath("list[].followee").description("팔로잉"),
                fieldWithPath("list[].merit").description("상점"),
                fieldWithPath("list[].demerit").description("벌점"),
                fieldWithPath("list[].generation").description("기수"),
                subsectionWithPath("list[].memberJobs").description("멤버 직책"),
                subsectionWithPath("list[].posting").description("작성한 게시물"),
                subsectionWithPath("list[].authorities").description("멤버 권한")
            )));
  }

  private MemberEntity generateTestMember(int point) {
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
        .generation(0F)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .point(point)
        .build();
    memberRepository.save(memberEntity);
    return memberEntity;
  }
}
