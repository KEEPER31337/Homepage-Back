package keeper.project.homepage.study.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.study.entity.StudyEntity;
import keeper.project.homepage.study.entity.StudyHasMemberEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
public class StudyControllerTest extends StudyControllerTestSetup {

  private String userToken1, userToken2, userToken3, userToken4;
  private MemberEntity memberEntity1, memberEntity2, memberEntity3, memberEntity4;
  private StudyEntity studyEntity;

  @BeforeEach
  public void setUp() throws Exception {
    memberEntity1 = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    memberEntity2 = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    memberEntity3 = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);
    memberEntity4 = generateMemberEntity(MemberJobName.회원, MemberTypeName.정회원, MemberRankName.일반회원);

    userToken1 = generateJWTToken(memberEntity1);
    userToken2 = generateJWTToken(memberEntity2);
    userToken3 = generateJWTToken(memberEntity3);
    userToken4 = generateJWTToken(memberEntity4);

    // member1이 스터디장, member2, member3은 스터디원, member4는 추가 될 스터디원
    studyEntity = generateStudyEntity(memberEntity1);
    addMember(studyEntity, memberEntity2);
    addMember(studyEntity, memberEntity3);
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }

  @Test
  @DisplayName("스터디 년도 불러오기 성공")
  public void getAllStudyYearsAndSeasonSuccess() throws Exception {
    String docMsg = "";
    String docCode = "";
    mockMvc.perform(get("/v1/study/years")
            .header("Authorization", userToken1))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("study-years",
            responseFields(
                generateStudyYearSeasonDtoResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", docCode, docMsg)
            )));
  }

  @Test
  @DisplayName("스터디 목록 불러오기 성공")
  public void getAllStudyListSuccess() throws Exception {
    String docMsg = "잘못된 시즌을 입력하면 실패합니다.";
    String docCode = "잘못된 시즌을 입력한 경우: " + exceptionAdvice.getMessage("seasonInvalid.code") + " +\n"
        + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");
    mockMvc.perform(get("/v1/study/list")
            .header("Authorization", userToken1)
            .param("year", String.valueOf(VALID_YEAR))
            .param("season", String.valueOf(VALID_SEASON)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andDo(document("study-list",
            requestParameters(
                parameterWithName("year").description("스터디 년도"),
                parameterWithName("season").description("스터디 시즌\n"
                    + "1: 1학기" + "\n"
                    + "2: 여름학기" + "\n"
                    + "3: 2학기" + "\n"
                    + "4: 겨울학기")
            ),
            responseFields(
                generateStudyDtoResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", docCode, docMsg)
            )));
    ;
  }

  @Test
  @DisplayName("스터디 목록 불러오기 실패 (잘못된 시즌)")
  public void getAllStudyListFailed() throws Exception {
    mockMvc.perform(get("/v1/study/list")
            .header("Authorization", userToken1)
            .param("year", String.valueOf(VALID_YEAR))
            .param("season", String.valueOf(INVALID_SEASON)))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(exceptionAdvice.getMessage("seasonInvalid.code")));
  }


  @Test
  @DisplayName("새로운 스터디 생성 성공")
  public void createStudySuccess() throws Exception {
    String docMsg = "잘못된 멤버, 잘못된 시즌을 입력하거나 IP를 공백으로 보내면 실패합니다.";
    String docCode = "잘못된 멤버를 입력한 경우: " + exceptionAdvice.getMessage("memberNotFound.code") + " +\n"
        + "잘못된 시즌을 입력한 경우: " + exceptionAdvice.getMessage("seasonInvalid.code") + " +\n"
        + "IP를 공백으로 입력한 경우: " + exceptionAdvice.getMessage("ipAddressNotFound.code") + " +\n"
        + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");

    List<MemberEntity> memberEntities = new ArrayList<>();
    memberEntities.add(memberEntity1);
    memberEntities.add(memberEntity2);
    memberEntities.add(memberEntity3);
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", getFileName(createTestImage),
        "image/jpg", new FileInputStream(userDirectory + File.separator + createTestImage));
    params.add("studyDto.title", NEW_TITLE);
    params.add("studyDto.information", NEW_INFORMATION);
    params.add("studyDto.year", String.valueOf(VALID_YEAR));
    params.add("studyDto.season", String.valueOf(VALID_SEASON));
    params.add("studyDto.ipAddress", "127.0.0.1");
    params.add("memberIdList[0]", String.valueOf(memberEntities.get(1).getId()));
    params.add("memberIdList[1]", String.valueOf(memberEntities.get(2).getId()));

    mockMvc.perform(multipart("/v1/study")
            .file(thumbnail)
            .header("Authorization", userToken1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("POST");
              return request;
            }))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(NEW_TITLE))
        .andExpect(jsonPath("$.data.memberNumber").value(memberEntities.size()))
        .andExpect(jsonPath("$.data.information").value(NEW_INFORMATION))
        .andExpect(jsonPath("$.data.year").value(VALID_YEAR))
        .andExpect(jsonPath("$.data.season").value(VALID_SEASON))
        .andDo(document("study-create",
            requestParameters(
                parameterWithName("studyDto.title").description("스터디 제목"),
                parameterWithName("studyDto.information").description("스터디 소개"),
                parameterWithName("studyDto.year").description("스터디 년도"),
                parameterWithName("studyDto.season").description("스터디 시즌\n"
                    + "1: 1학기" + "\n"
                    + "2: 여름학기" + "\n"
                    + "3: 2학기" + "\n"
                    + "4: 겨울학기"),
                parameterWithName("studyDto.gitLink").description("github 주소").optional(),
                parameterWithName("studyDto.noteLink").description("notion 주소").optional(),
                parameterWithName("studyDto.etcLink").description("그 외 스터디 링크").optional(),
                parameterWithName("studyDto.ipAddress").description("IP 주소"),
                parameterWithName("memberIdList[0]").description(
                    "추가할 스터디원 id (스터디 생성자는 자동으로 추가됩니다.)"),
                parameterWithName("memberIdList[1]").description(
                    "추가할 스터디원 id (스터디 생성자는 자동으로 추가됩니다.)")
            ),
            requestParts(
                partWithName("thumbnail").description(
                    "썸네일 용 이미지 (form-data 에서 thumbnail= parameter 부분)")
            ),
            responseFields(
                generateStudyDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", docCode, docMsg)
            )));
  }

  @Test
  @DisplayName("새로운 스터디 생성 실패 (잘못된 멤버 입력)")
  public void createStudyFailedWithMember() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", getFileName(createTestImage),
        "image/jpg", new FileInputStream(userDirectory + File.separator + createTestImage));
    params.add("studyDto.title", NEW_TITLE);
    params.add("studyDto.information", NEW_INFORMATION);
    params.add("studyDto.year", String.valueOf(VALID_YEAR));
    params.add("studyDto.season", String.valueOf(VALID_SEASON));
    params.add("studyDto.gitLink", GIT_LINK);
    params.add("studyDto.noteLink", NOTE_LINK);
    params.add("studyDto.ipAddress", "127.0.0.1");
    params.add("memberIdList[0]", String.valueOf(memberEntity2.getId()));
    params.add("memberIdList[1]", String.valueOf(INVALID_MEMBER_ID));

    mockMvc.perform(multipart("/v1/study")
            .file(thumbnail)
            .header("Authorization", userToken1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("POST");
              return request;
            }))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  @DisplayName("스터디 수정 성공")
  public void modifyStudySuccess() throws Exception {
    String docMsg = "잘못된 스터디 번호, 잘못된 시즌을 입력하거나 IP를 공백으로 보내거나\n"
        + "스터디장이 아닌데 수정을 시도하면 실패합니다.";
    String docCode =
        "잘못된 스터디 번호를 입력한 경우: " + exceptionAdvice.getMessage("studyNotFound.code") + " +\n"
            + "잘못된 시즌을 입력한 경우: " + exceptionAdvice.getMessage("seasonInvalid.code") + " +\n"
            + "IP를 공백으로 입력한 경우: " + exceptionAdvice.getMessage("ipAddressNotFound.code") + " +\n"
            + "스터디장이 아닌 사람이 스터디를 수정하려는 경우: " + exceptionAdvice.getMessage("studyNotMine.code")
            + " +\n"
            + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");

    List<MemberEntity> memberEntities = new ArrayList<>();
    memberEntities.add(memberEntity1);
    memberEntities.add(memberEntity2);
    memberEntities.add(memberEntity4);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", getFileName(createTestImage),
        "image/jpg", new FileInputStream(userDirectory + File.separator + createTestImage));
    params.add("studyDto.title", MODIFY_TITLE);
    params.add("studyDto.information", MODIFY_INFORMATION);
    params.add("studyDto.year", String.valueOf(MODIFY_VALID_YEAR));
    params.add("studyDto.season", String.valueOf(MODIFY_VALID_SEASON));
    params.add("studyDto.gitLink", GIT_LINK);
    params.add("studyDto.noteLink", NOTE_LINK);
    params.add("studyDto.ipAddress", "127.0.0.2");
    params.add("studyId", String.valueOf(studyEntity.getId()));
    params.add("memberIdList[0]", String.valueOf(memberEntities.get(0).getId()));
    params.add("memberIdList[1]", String.valueOf(memberEntities.get(1).getId()));
    params.add("memberIdList[2]", String.valueOf(memberEntities.get(2).getId()));

    mockMvc.perform(multipart("/v1/study")
            .file(thumbnail)
            .header("Authorization", userToken1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.data.title").value(MODIFY_TITLE))
        .andExpect(jsonPath("$.data.information").value(MODIFY_INFORMATION))
        .andExpect(jsonPath("$.data.year").value(MODIFY_VALID_YEAR))
        .andExpect(jsonPath("$.data.season").value(MODIFY_VALID_SEASON))
        .andDo(document("study-modify",
            requestParameters(
                parameterWithName("studyDto.title").description("스터디 제목"),
                parameterWithName("studyDto.information").description("스터디 소개"),
                parameterWithName("studyDto.year").description("스터디 년도"),
                parameterWithName("studyDto.season").description("스터디 시즌\n"
                    + "1: 1학기" + "\n"
                    + "2: 여름학기" + "\n"
                    + "3: 2학기" + "\n"
                    + "4: 겨울학기"),
                parameterWithName("studyDto.gitLink").description("github 주소").optional(),
                parameterWithName("studyDto.noteLink").description("notion 주소").optional(),
                parameterWithName("studyDto.etcLink").description("그 외 스터디 링크").optional(),
                parameterWithName("studyDto.ipAddress").description("IP 주소"),
                parameterWithName("studyId").description("스터디 Id"),
                parameterWithName("memberIdList[0]").description(
                    "새로운 스터디원들 Id List. 스터디원 Id들 전부 보내주시면 됩니다."),
                parameterWithName("memberIdList[1]").description(
                    "새로운 스터디원들 Id List. 스터디원 Id들 전부 보내주시면 됩니다."),
                parameterWithName("memberIdList[2]").description(
                    "새로운 스터디원들 Id List. 스터디원 Id들 전부 보내주시면 됩니다.")
            ),
            requestParts(
                partWithName("thumbnail").description(
                    "썸네일 용 이미지 (form-data 에서 thumbnail= parameter 부분)")
            ),
            responseFields(
                generateStudyDtoResponseFields(ResponseType.SINGLE,
                    "성공: true +\n실패: false", docCode, docMsg)
            )));

    List<Long> studyMemberIdList = studyHasMemberRepository.findAllByStudyId(studyEntity.getId())
        .stream().map(StudyHasMemberEntity::getMember).map(MemberEntity::getId).toList();
    List<Long> modifyMemberIdList = memberEntities.stream().map(MemberEntity::getId).toList();
    Assertions.assertThat(studyMemberIdList).isEqualTo(modifyMemberIdList);
  }

  // 시즌 실패, IP주소 없음 실패는 modify와 create에 중복되서 하나만 만들었습니다.
  @Test
  @DisplayName("스터디 수정 실패 (잘못된 시즌 입력)")
  public void modifyStudyFailedWithSeason() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", getFileName(createTestImage),
        "image/jpg", new FileInputStream(userDirectory + File.separator + createTestImage));
    params.add("studyDto.title", MODIFY_TITLE);
    params.add("studyDto.information", MODIFY_INFORMATION);
    params.add("studyDto.year", String.valueOf(MODIFY_VALID_YEAR));
    params.add("studyDto.season", String.valueOf(INVALID_SEASON));
    params.add("studyDto.ipAddress", "127.0.0.2");
    params.add("studyId", String.valueOf(studyEntity.getId()));

    mockMvc.perform(multipart("/v1/study")
            .file(thumbnail)
            .header("Authorization", userToken1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(exceptionAdvice.getMessage("seasonInvalid.code")));
  }

  @Test
  @DisplayName("스터디 수정 실패 (IP 주소 없음)")
  public void modifyStudyFailedWithIp() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", getFileName(createTestImage),
        "image/jpg", new FileInputStream(userDirectory + File.separator + createTestImage));
    params.add("studyDto.title", MODIFY_TITLE);
    params.add("studyDto.information", MODIFY_INFORMATION);
    params.add("studyDto.year", String.valueOf(MODIFY_VALID_YEAR));
    params.add("studyDto.season", String.valueOf(MODIFY_VALID_SEASON));
    /* IP 주소 없음 테스트 */
    params.add("studyDto.ipAddress", "");
    params.add("studyId", String.valueOf(studyEntity.getId()));

    mockMvc.perform(multipart("/v1/study")
            .file(thumbnail)
            .header("Authorization", userToken1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(exceptionAdvice.getMessage("ipAddressNotFound.code")));
  }

  @Test
  @DisplayName("스터디 수정 실패 (스터디장 아님)")
  public void modifyStudyFailedWithIsMine() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", getFileName(createTestImage),
        "image/jpg", new FileInputStream(userDirectory + File.separator + createTestImage));
    params.add("studyDto.title", MODIFY_TITLE);
    params.add("studyDto.information", MODIFY_INFORMATION);
    params.add("studyDto.year", String.valueOf(MODIFY_VALID_YEAR));
    params.add("studyDto.season", String.valueOf(MODIFY_VALID_SEASON));
    params.add("studyDto.ipAddress", "127.0.0.2");
    params.add("studyId", String.valueOf(studyEntity.getId()));

    mockMvc.perform(multipart("/v1/study")
            .file(thumbnail)
            .header("Authorization", userToken2)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(exceptionAdvice.getMessage("studyNotMine.code")));
  }

  @Test
  @DisplayName("스터디원 추가 성공")
  public void addMemberSuccess() throws Exception {
    Long studyMemberCount = studyHasMemberRepository.countByStudy(studyEntity);
    String docMsg = "잘못된 스터디 번호, 스터디장이 아닌데 수정을 시도하면 실패합니다.";
    String docCode =
        "잘못된 스터디 번호를 입력한 경우: " + exceptionAdvice.getMessage("studyNotFound.code") + " +\n"
            + "스터디장이 아닌 사람이 스터디원을 추가하려는 경우: " + exceptionAdvice.getMessage("studyNotMine.code")
            + " +\n"
            + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");

    String content = "{\n"
        + "    \"studyId\": \"" + studyEntity.getId() + "\",\n"
        + "    \"memberId\": \"" + memberEntity4.getId() + "\"\n"
        + "}";
    mockMvc.perform(patch("/v1/study/member/add")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", userToken1)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.list.length()").value(studyMemberCount + 1))
        .andDo(document("study-member-add",
            requestFields(
                fieldWithPath("studyId").description("스터디원을 추가할 스터디 Id"),
                fieldWithPath("memberId").description("추가할 스터디원 Id (이미 존재할 경우 아무일도 일어나지 않습니다)")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", docCode, docMsg)
            )));
  }

  @Test
  @DisplayName("스터디원 추가 실패 (스터디장 아님)")
  public void addMemberFailedWithIsMine() throws Exception {
    String content = "{\n"
        + "    \"studyId\": \"" + studyEntity.getId() + "\",\n"
        + "    \"memberId\": \"" + memberEntity4.getId() + "\"\n"
        + "}";
    mockMvc.perform(patch("/v1/study/member/add")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", userToken2)
            .content(content))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(exceptionAdvice.getMessage("studyNotMine.code")));
  }

  @Test
  @DisplayName("스터디원 추가 실패 (잘못된 멤버 ID)")
  public void addMemberFailedWithMember() throws Exception {
    String content = "{\n"
        + "    \"studyId\": \"" + studyEntity.getId() + "\",\n"
        + "    \"memberId\": \"" + INVALID_MEMBER_ID + "\"\n"
        + "}";
    mockMvc.perform(patch("/v1/study/member/add")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", userToken1)
            .content(content))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  @DisplayName("스터디원 삭제 성공")
  public void removeMemberSuccess() throws Exception {
    Long studyMemberCount = studyHasMemberRepository.countByStudy(studyEntity);
    String docMsg = "잘못된 스터디 번호, 스터디장이 아닌데 수정을 시도하면 실패합니다.";
    String docCode =
        "잘못된 스터디 번호를 입력한 경우: " + exceptionAdvice.getMessage("studyNotFound.code") + " +\n"
            + "스터디장이 아닌 사람이 스터디원을 삭제하려는 경우: " + exceptionAdvice.getMessage("studyNotMine.code")
            + " +\n"
            + "그 외 에러가 발생한 경우: " + exceptionAdvice.getMessage("unKnown.code");

    String content = "{\n"
        + "    \"studyId\": \"" + studyEntity.getId() + "\",\n"
        + "    \"memberId\": \"" + memberEntity2.getId() + "\"\n"
        + "}";
    mockMvc.perform(patch("/v1/study/member/remove")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", userToken1)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.list.length()").value(studyMemberCount - 1))
        .andDo(document("study-member-remove",
            requestFields(
                fieldWithPath("studyId").description("스터디원을 삭제할 스터디 Id"),
                fieldWithPath("memberId").description("삭제할 스터디원 Id (스터디원이 아닐 경우 아무일도 일어나지 않습니다)")
            ),
            responseFields(
                generateMemberCommonResponseFields(ResponseType.LIST,
                    "성공: true +\n실패: false", docCode, docMsg)
            )));
  }

  @Test
  @DisplayName("스터디원 삭제 성공 (스터디원이 아닌 member)")
  public void removeMemberSuccessWithNotExist() throws Exception {
    Long studyMemberCount = studyHasMemberRepository.countByStudy(studyEntity);

    String content = "{\n"
        + "    \"studyId\": \"" + studyEntity.getId() + "\",\n"
        + "    \"memberId\": \"" + memberEntity4.getId() + "\"\n"
        + "}";
    mockMvc.perform(patch("/v1/study/member/remove")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", userToken1)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.list.length()").value(studyMemberCount));
  }
}
