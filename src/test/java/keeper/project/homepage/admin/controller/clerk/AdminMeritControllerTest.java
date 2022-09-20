package keeper.project.homepage.admin.controller.clerk;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.서기;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.clerk.dto.request.MeritAddRequestDto;
import keeper.project.homepage.clerk.dto.request.MeritLogUpdateRequestDto;
import keeper.project.homepage.clerk.dto.request.MeritTypeCreateRequestDto;
import keeper.project.homepage.clerk.entity.MeritLogEntity;
import keeper.project.homepage.clerk.entity.MeritTypeEntity;
import keeper.project.homepage.member.entity.MemberEntity;
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
  public void createMeritsLogs() throws Exception {
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");
    MeritTypeEntity absence = generateMeritType(3, false, "결석");

    MemberEntity awarder1 = generateMember("이정학", 12.5F);
    MemberEntity awarder2 = generateMember("정현모", 8F);
    List<MeritAddRequestDto> requestDtoList = new ArrayList<>();
    requestDtoList.add(getMeritAddRequestDto(publicAnnouncement, awarder1));
    requestDtoList.add(getMeritAddRequestDto(absence, awarder2));
    requestDtoList.add(getMeritAddRequestDto(publicAnnouncement, awarder2));

    mockMvc.perform(post("/v1/admin/clerk/merits")
            .header("Authorization", clerkToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(requestDtoList)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("add-merit-log-list",
            requestFields(fieldWithPath("[].date").description("상벌점 내역 추가 날짜"),
                fieldWithPath("[].memberId").description("상벌점 수여자 id"),
                fieldWithPath("[].meritTypeId").description("상벌점 종류")),
            responseFields(fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[]").description("추가한 상벌점 내역 id 리스트"))));
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 내역 삭제")
  void deleteMeritWithLogTest() throws Exception {
    MemberEntity awarder = generateMember("이정학", 12.5F);
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");

    Long meritId = generateMeritLog(awarder, clerk, publicAnnouncement,
        LocalDate.now()).getId();

    mockMvc.perform(delete("/v1/admin/clerk/merits/{meritId}", meritId)
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data").value(meritId))
        .andDo(document("delete-merit-log",
            pathParameters(
                parameterWithName("meritId").description("삭제할 상벌점 내역 id")),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("삭제한 상벌점 내역 id"))));
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 내역 수정")
  void updateMerit() throws Exception {
    MemberEntity giver = clerk;
    MemberEntity awarder = generateMember("이정학", 12.5F);
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");
    MeritTypeEntity bestTechDoc = generateMeritType(3, true, "우수기술문서작성");
    MeritLogEntity meritLog = generateMeritLog(awarder, giver, publicAnnouncement, LocalDate.now());
    awarder.changeMerit(2);

    MeritLogUpdateRequestDto requestDto = getMeritLogUpdateRequestDto(bestTechDoc, meritLog);

    mockMvc.perform(patch("/v1/admin/clerk/merits")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonDateString(requestDto))
            .header("Authorization", clerkToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.data").value(meritLog.getId()))
        .andDo(document("update-merit-log",
            requestFields(
                fieldWithPath("meritLogId").description("수정할 상벌점 내역 id"),
                fieldWithPath("meritTypeId").description("수정할 상벌점 타입"),
                fieldWithPath("date").description("수정할 날짜")),
            responseFields(fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data").description("수정한 상벌점 내역 id"))));
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

    mockMvc.perform(get("/v1/admin/clerk/merits/total").header("Authorization", clerkToken))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0)).andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andExpect(jsonPath("$.list.[0].memberId").value(awarder.getId()))
        .andExpect(jsonPath("$.list.[0].totalMerit").value(8))
        .andExpect(jsonPath("$.list.[0].totalDemerit").value(3))
        .andExpect(jsonPath("$.list.[0].detailsWithCount.size()").value(3))
        .andDo(document("get-merit-log-total",
            responseFields(fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[].memberId").description("회원 id"),
                fieldWithPath("list.[].realName").description("회원 이름"),
                fieldWithPath("list.[].totalMerit").description("회원의 상점 누계"),
                fieldWithPath("list.[].totalDemerit").description("회원의 벌점 누계"),
                fieldWithPath("list.[].detailsWithCount.*").description(
                    "상벌점 사유 목록과 각 목록 당 누적 횟수)"))));
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
            responseFields(fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[].meritLogId").description("상벌점 내역 id"),
                fieldWithPath("list.[].awarderRealName").description("상벌점 수상자 이름"),
                fieldWithPath("list.[].date").description("상벌점 수여 날짜"),
                fieldWithPath("list.[].isMerit").description("상점이면 1, 벌점이면 0"),
                fieldWithPath("list.[].merit").description("상벌점 점수"),
                fieldWithPath("list.[].detail").description("상벌점 사유"))));
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
                fieldWithPath("list").description("상벌점 내역 년도 리스트"))));
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 타입 목록 조회")
  public void getMeritTypes() throws Exception {
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");
    MeritTypeEntity manyTechDoc = generateMeritType(3, true, "연2개이상의기술문서작성");
    MeritTypeEntity absence = generateMeritType(3, false, "결석");

    mockMvc.perform(get("/v1/admin/clerk/merits/types").header("Authorization", clerkToken))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0)).andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("get-merit-types",
            responseFields(fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list.[].id").description("상벌점 타입 id"),
                fieldWithPath("list.[].merit").description("상벌점 점수"),
                fieldWithPath("list.[].isMerit").description("상점이면 1 벌점이면 0"),
                fieldWithPath("list.[].detail").description("상벌점 사유"))));
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 타입 생성")
  public void createMeritType() throws Exception {
    MeritTypeCreateRequestDto request1 = getMeritTypeCreateRequestDto(2, true, "개근상");
    MeritTypeCreateRequestDto request2 = getMeritTypeCreateRequestDto(3, true, "우수기술문서작성");

    mockMvc.perform(post("/v1/admin/clerk/merits/types")
            .header("Authorization", clerkToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(List.of(request1, request2))))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("create-merit-types",
            requestFields(fieldWithPath("[]merit").description("추가할 상벌점 점수"),
                fieldWithPath("[].isMerit").description("상점이면 1 벌점이면 0"),
                fieldWithPath("[].detail").description("추가할 상벌점 사유")),
            responseFields(fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list").description("추가한 상벌점 타입 id"))));
  }

  @Test
  @DisplayName("[SUCCESS] 상벌점 타입 삭제")
  public void deleteMeritType() throws Exception {
    MeritTypeEntity publicAnnouncement = generateMeritType(2, true, "각종대외발표");
    MeritTypeEntity bestTechDoc = generateMeritType(3, true, "우수기술문서작성");
    String param = publicAnnouncement.getId() + "," + bestTechDoc.getId();
    mockMvc.perform(
            delete("/v1/admin/clerk/merits/types", publicAnnouncement.getId())
                .header("Authorization", clerkToken)
                .param("typeIds", param))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").value("성공하였습니다."))
        .andDo(document("delete-merit-types",
            requestParameters(
                parameterWithName("typeIds").description("삭제할 상벌점 타입 id 리스트")),
            responseFields(fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list").description("삭제한 상벌점 타입 id"))));
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
        .date(date)
        .build());
  }

  private static MeritAddRequestDto getMeritAddRequestDto(MeritTypeEntity publicAnnouncement,
      MemberEntity awarder) {
    return MeritAddRequestDto.builder()
        .date(LocalDate.now())
        .memberId(awarder.getId())
        .meritTypeId(publicAnnouncement.getId())
        .build();
  }

  private static MeritTypeCreateRequestDto getMeritTypeCreateRequestDto(Integer merit,
      Boolean isMerit, String detail) {
    return MeritTypeCreateRequestDto.builder().merit(merit).isMerit(isMerit).detail(detail).build();
  }

  private static MeritLogUpdateRequestDto getMeritLogUpdateRequestDto(MeritTypeEntity type,
      MeritLogEntity meritLog) {
    return MeritLogUpdateRequestDto.builder()
        .meritLogId(meritLog.getId())
        .meritTypeId(type.getId())
        .date(LocalDate.now())
        .build();
  }
}
