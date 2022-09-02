package keeper.project.homepage.admin.controller.clerk;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.서기;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import keeper.project.homepage.admin.dto.clerk.request.MeritAddRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.MeritTypeCreateRequestDto;
import keeper.project.homepage.entity.clerk.MeritLogEntity;
import keeper.project.homepage.entity.clerk.MeritTypeEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class AdminMeritControllerTest extends ClerkControllerTestHelper {

  private MemberEntity clerk;
  private String clerkToken;

  @BeforeEach
  public void setUp() throws Exception {
    clerk = generateMemberEntity(서기, 정회원, 우수회원);
    clerkToken = generateJWTToken(clerk);
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 내역 추가")
  public void createMeritLog() throws Exception {
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");
    MemberEntity awarder = generateMember("이정학", 12.5F);
    MeritAddRequestDto meritLogCreateRequestDto = MeritAddRequestDto.builder()
        .date(LocalDate.now())
        .memberId(awarder.getId())
        .meritTypeId(publicAnnouncement.getId())
        .build();
    mockMvc.perform(post("/v1/admin/clerk/merits")
            .header("Authorization", clerkToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(meritLogCreateRequestDto))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("create-merit-log",
            requestFields(
                fieldWithPath("date").description("상벌점 내역 추가 날짜"),
                fieldWithPath("memberId").description("상벌점 수여자 id"),
                fieldWithPath("meritTypeId").description("상벌점 종류")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.meritLogId").description("추가한 상벌점 내역 id")
            ))
        );
  }

  @Test
  @DisplayName("[SUCCESS] 회원별 상벌점 누계 조회")
  public void getMeritLogByMember() throws Exception {
    MemberEntity giver = clerk;
    MemberEntity awarder = generateMember("이정학", 12.5F);
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");
    MeritTypeEntity manyTechDoc = generateMeritType(3, true, "연2개이상의기술문서작성");
    MeritTypeEntity absence = generateMeritType(3, false, "결석");
    LocalDate now = LocalDate.now();

    generateMeritLog(awarder, giver, manyTechDoc, now);
    generateMeritLog(awarder, giver, manyTechDoc, now);
    generateMeritLog(awarder, giver, publicAnnouncement, now);
    generateMeritLog(awarder, giver, absence, now);

    mockMvc.perform(get("/v1/admin/clerk/merits/total")
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.[0].memberId").value(awarder.getId()))
        .andExpect(jsonPath("$.list.[0].totalMerit").value(8))
        .andExpect(jsonPath("$.list.[0].totalDemerit").value(3))
        .andExpect(jsonPath("$.list.[0].details.size()").value(4))
        .andDo(document("get-merit-log-total",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[].memberId").description("회원 id"),
                fieldWithPath("list.[].totalMerit").description("회원의 상점 누계"),
                fieldWithPath("list.[].totalDemerit").description("회원의 벌점 누계"),
                fieldWithPath("list.[].details").description("상벌점 사유 목록")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 년도별 상벌점 내역 조회")
  public void getMeritLogByYear() throws Exception {
    MemberEntity giver = clerk;
    MemberEntity awarder = generateMember("이정학", 12.5F);
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");
    MeritTypeEntity manyTechDoc = generateMeritType(3, true, "연2개이상의기술문서작성");
    MeritTypeEntity absence = generateMeritType(3, false, "결석");
    LocalDate now = LocalDate.now();
    LocalDate lastYear = now.minusYears(1);

    generateMeritLog(awarder, giver, manyTechDoc, now);
    generateMeritLog(awarder, giver, manyTechDoc, lastYear);
    generateMeritLog(awarder, giver, publicAnnouncement, lastYear);
    generateMeritLog(awarder, giver, absence, lastYear);

    mockMvc.perform(get("/v1/admin/clerk/merits")
            .header("Authorization", clerkToken)
            .param("year", String.valueOf(lastYear.getYear())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.size()").value(3))
        .andDo(document("get-merit-log-by-year",
            requestParameters(parameterWithName("year").description("조회할 년도")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[].memberId").description("회원 id"),
                fieldWithPath("list.[].date").description("상벌점 수여 날짜"),
                fieldWithPath("list.[].isMerit").description("상점이면 1, 벌점이면 0"),
                fieldWithPath("list.[].merit").description("상벌점 점수"),
                fieldWithPath("list.[].detail").description("상벌점 사유")
            )));
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 내역 년도 리스트 조회")
  public void getYears() throws Exception {
    MemberEntity giver = clerk;
    MemberEntity awarder = generateMember("이정학", 12.5F);
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");
    LocalDate now = LocalDate.now();
    LocalDate oldest = now.minusYears(5);
    int latestYear = now.getYear();
    int oldestYear = oldest.getYear();

    generateMeritLog(awarder, giver, publicAnnouncement, oldest);

    mockMvc.perform(get("/v1/admin/clerk/merits/years")
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.size()").value(latestYear - oldestYear + 1))
        .andDo(document("get-merits-year-list",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list").description("상벌점 내역 년도 리스트")))
        );
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 타입 목록 조회")
  public void getMeritTypes() throws Exception {
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");
    MeritTypeEntity manyTechDoc = generateMeritType(3, true, "연2개이상의기술문서작성");
    MeritTypeEntity absence = generateMeritType(3, false, "결석");

    mockMvc.perform(get("/v1/admin/clerk/merits/types")
            .header("Authorization", clerkToken)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("get-merit-types",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[].id").description("상벌점 타입 id"),
                fieldWithPath("list.[].merit").description("상벌점 점수"),
                fieldWithPath("list.[].isMerit").description("상점이면 1 벌점이면 0"),
                fieldWithPath("list.[].detail").description("상벌점 사유")
            ))
        );
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 타입 추가")
  public void createMeritType() throws Exception {
    MeritTypeCreateRequestDto meritTypeCreateRequestDto = MeritTypeCreateRequestDto.builder()
        .merit(3)
        .isMerit(true)
        .detail("키퍼상")
        .build();

    mockMvc.perform(post("/v1/admin/clerk/merits/types")
            .header("Authorization", clerkToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(meritTypeCreateRequestDto))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("create-merit-type",
            requestFields(
                fieldWithPath("merit").description("추가할 상벌점 점수"),
                fieldWithPath("isMerit").description("상점이면 1 벌점이면 0"),
                fieldWithPath("detail").description("추가할 상벌점 사유")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("추가한 상벌점 타입 id")
            ))
        );
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 타입 삭제")
  public void deleteMeritType() throws Exception {
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");

    mockMvc.perform(delete("/v1/admin/clerk/merits/types/{typeId}", publicAnnouncement.getId())
            .header("Authorization", clerkToken)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("delete-merit-type",
            pathParameters(parameterWithName("typeId").description("삭제할 상벌점 타입 id")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("삭제한 상벌점 타입 id")
            ))
        );
  }

  private MeritTypeEntity generateMeritType(Integer merit, Boolean isMerit, String detail) {
    return meritTypeRepository.save(MeritTypeEntity.newInstance(merit, isMerit, detail));
  }

  private MeritLogEntity generateMeritLog(MemberEntity awarder, MemberEntity giver,
      MeritTypeEntity type, LocalDate date) {
    return meritLogRepository.save(MeritLogEntity.builder()
        .awarder(awarder)
        .giver(giver)
        .meritType(type)
        .time(date)
        .build());
  }
}
