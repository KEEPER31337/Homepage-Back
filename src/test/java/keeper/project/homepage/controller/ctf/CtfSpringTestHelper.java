package keeper.project.homepage.controller.ctf;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.ctf.CtfDynamicChallengeInfoEntity;
import keeper.project.homepage.entity.ctf.CtfFlagEntity;
import keeper.project.homepage.entity.ctf.CtfSubmitLogEntity;
import keeper.project.homepage.entity.ctf.CtfTeamEntity;
import keeper.project.homepage.entity.ctf.CtfTeamHasMemberEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.ctf.CtfChallengeCategoryRepository;
import keeper.project.homepage.repository.ctf.CtfChallengeRepository;
import keeper.project.homepage.repository.ctf.CtfChallengeTypeRepository;
import keeper.project.homepage.repository.ctf.CtfContestRepository;
import keeper.project.homepage.repository.ctf.CtfDynamicChallengeInfoRepository;
import keeper.project.homepage.repository.ctf.CtfFlagRepository;
import keeper.project.homepage.repository.ctf.CtfSubmitLogRepository;
import keeper.project.homepage.repository.ctf.CtfTeamHasMemberRepository;
import keeper.project.homepage.repository.ctf.CtfTeamRepository;
import keeper.project.homepage.util.service.CtfUtilService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.FieldDescriptor;

public class CtfSpringTestHelper extends ApiControllerTestHelper {

  @Autowired
  protected CtfChallengeCategoryRepository ctfChallengeCategoryRepository;

  @Autowired
  protected CtfChallengeTypeRepository ctfChallengeTypeRepository;

  @Autowired
  protected CtfChallengeRepository ctfChallengeRepository;

  @Autowired
  protected CtfContestRepository ctfContestRepository;

  @Autowired
  protected CtfFlagRepository ctfFlagRepository;

  @Autowired
  protected CtfSubmitLogRepository ctfSubmitLogRepository;

  @Autowired
  protected CtfTeamRepository ctfTeamRepository;

  @Autowired
  protected CtfTeamHasMemberRepository ctfTeamHasMemberRepository;

  @Autowired
  protected CtfDynamicChallengeInfoRepository ctfDynamicChallengeInfoRepository;

  @Autowired
  protected CtfUtilService ctfUtilService;

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonContent = mapper.writeValueAsString(obj);
      return jsonContent;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @RequiredArgsConstructor
  @Getter
  protected enum CtfChallengeCategory {
    Misc(1L),
    System(2L),
    Reversing(3L),
    Forensic(4L),
    Web(5L),
    Crypto(6L);

    private final Long id;
  }

  @RequiredArgsConstructor
  @Getter
  protected enum CtfChallengeType {
    STANDARD(1L),
    DYNAMIC(2L);

    private final Long id;
  }

  protected CtfContestEntity generateCtfContest(MemberEntity creator, boolean isJoinable) {
    final long epochTime = System.nanoTime();
    CtfContestEntity entity = CtfContestEntity.builder()
        .name("name_" + epochTime)
        .description("desc_" + epochTime)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .isJoinable(isJoinable)
        .build();
    ctfContestRepository.save(entity);
    return entity;
  }

  protected CtfContestEntity generateCtfContest(MemberEntity creator) {
    final long epochTime = System.nanoTime();
    CtfContestEntity entity = CtfContestEntity.builder()
        .name("name_" + epochTime)
        .description("desc_" + epochTime)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .isJoinable(false)
        .build();
    ctfContestRepository.save(entity);
    return entity;
  }

  protected CtfFlagEntity generateCtfFlag(CtfTeamEntity ctfTeam, CtfChallengeEntity ctfChallenge,
      Boolean isCorrect) {
    final long epochTime = System.nanoTime();
    CtfFlagEntity entity = CtfFlagEntity.builder()
        .content("flag_" + epochTime)
        .ctfTeamEntity(ctfTeam)
        .ctfChallengeEntity(ctfChallenge)
        .isCorrect(isCorrect)
        .build();
    ctfFlagRepository.save(entity);
    ctfChallenge.getCtfFlagEntity().add(entity);
    return entity;
  }

  protected CtfSubmitLogEntity generateCtfSubmitLog(CtfTeamEntity ctfTeam, MemberEntity submitter,
      CtfChallengeEntity ctfChallengeEntity, String submitFlag) {
    final long epochTime = System.nanoTime();
    CtfSubmitLogEntity entity = CtfSubmitLogEntity.builder()
        .submitTime(LocalDateTime.now())
        .ctfTeamEntity(ctfTeam)
        .submitter(submitter)
        .ctfChallengeEntity(ctfChallengeEntity)
        .flagSubmitted(submitFlag)
        .isCorrect(false)
        .build();
    ctfSubmitLogRepository.save(entity);
    return entity;
  }

  protected CtfTeamEntity generateCtfTeam(CtfContestEntity ctfContestEntity, MemberEntity creator,
      Long score) {
    final long epochTime = System.nanoTime();
    CtfTeamEntity entity = CtfTeamEntity.builder()
        .name("name_" + epochTime)
        .description("desc_" + epochTime)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .score(score)
        .ctfContestEntity(ctfContestEntity)
        .build();
    ctfTeamRepository.save(entity);

    CtfTeamHasMemberEntity teamHasMemberEntity = CtfTeamHasMemberEntity.builder()
        .team(entity)
        .member(creator)
        .build();
    ctfTeamHasMemberRepository.save(teamHasMemberEntity);
    entity.getCtfTeamHasMemberEntityList().add(teamHasMemberEntity);
    return entity;
  }

  protected CtfChallengeEntity generateCtfChallenge(
      CtfContestEntity ctfContestEntity,
      CtfChallengeTypeEntity ctfChallengeTypeEntity,
      CtfChallengeCategoryEntity ctfChallengeCategoryEntity,
      Long score) {
    final long epochTime = System.nanoTime();
    CtfChallengeEntity entity = CtfChallengeEntity.builder()
        .name("name_" + epochTime)
        .description("desc_" + epochTime)
        .registerTime(LocalDateTime.now())
        .creator(memberRepository.getById(1L)) // Virtual Member
        .isSolvable(false)
        .ctfChallengeTypeEntity(ctfChallengeTypeEntity)
        .ctfChallengeCategoryEntity(ctfChallengeCategoryEntity)
        .score(score)
        .ctfContestEntity(ctfContestEntity)
        .ctfFlagEntity(new ArrayList<>())
        .build();
    ctfChallengeRepository.save(entity);
    return entity;
  }

  protected FileEntity generateFileInChallenge(CtfChallengeEntity challenge) {
    FileEntity file = generateFileEntity();
    challenge.setFileEntity(file);
    ctfChallengeRepository.save(challenge);
    return file;
  }

  protected CtfDynamicChallengeInfoEntity generateDynamicChallengeInfo(
      CtfChallengeEntity ctfChallengeEntity, Long maxScore, Long minScore) {
    CtfDynamicChallengeInfoEntity entity = CtfDynamicChallengeInfoEntity.builder()
        .challengeId(ctfChallengeEntity.getId())
        .ctfChallengeEntity(ctfChallengeEntity)
        .maxScore(maxScore)
        .minScore(minScore)
        .build();
    ctfChallengeEntity.setDynamicChallengeInfoEntity(entity);
    ctfChallengeRepository.save(ctfChallengeEntity);
    return entity;
  }

  protected List<FieldDescriptor> generateContestDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".ctfId").description("해당 CTF의 Id"),
        fieldWithPath(prefix + ".name").description("CTF명"),
        fieldWithPath(prefix + ".description").description("CTF 부가 설명"),
        fieldWithPath(prefix + ".joinable").description("CTF에 현재 참석 가능 한지 아닌지"),
        subsectionWithPath(prefix + ".creatorId").description("생성자의 id가 담겨 나갑니다.")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateProbMakerDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".memberId").description("지정한 출제자의 member id"),
        fieldWithPath(prefix + ".nickName").description("지정한 출제자의 nickname"),
        fieldWithPath(prefix + ".thumbnailPath").description("지정한 출제자의 thumbnail path"),
        fieldWithPath(prefix + ".generation").description("지정한 출제자의 기수")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateChallengeCommonDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".challengeId").description("해당 문제의 Id"),
        fieldWithPath(prefix + ".title").description("문제 제목"),
        fieldWithPath(prefix + ".category.id").description("문제가 속한 카테고리의 id"),
        fieldWithPath(prefix + ".category.name").description("문제가 속한 카테고리의 이름"),
        fieldWithPath(prefix + ".score").description("문제의 점수"),
        fieldWithPath(prefix + ".contestId").description("문제의 대회 Id")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateChallengeAdminDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".challengeId").description("해당 문제의 Id"),
        fieldWithPath(prefix + ".title").description("문제 제목"),
        fieldWithPath(prefix + ".content").description("문제 설명"),
        fieldWithPath(prefix + ".category.id").description("문제가 속한 카테고리의 id"),
        fieldWithPath(prefix + ".category.name").description("문제가 속한 카테고리의 이름"),
        fieldWithPath(prefix + ".type.id").description("문제가 속한 타입의 id"),
        fieldWithPath(prefix + ".type.name").description("문제가 속한 타입의 이름"),
        fieldWithPath(prefix + ".flag").description("문제에 설정 된 flag (현재는 모든 팀이 동일한 flag를 가집니다."),
        fieldWithPath(prefix + ".score").description("문제의 점수"),
        fieldWithPath(prefix + ".creatorName").description("문제 생성자 이름"),
        fieldWithPath(prefix + ".contestId").description("문제의 대회 Id"),
        fieldWithPath(prefix + ".registerTime").description("문제의 등록 시간"),
        fieldWithPath(prefix + ".isSolvable").description("현재 풀 수 있는 지 여부"),
        fieldWithPath(prefix + ".dynamicInfo.maxScore").description("TYPE이 DYNAMIC일 경우 maxScore")
            .optional(),
        fieldWithPath(prefix + ".dynamicInfo.minScore").description("TYPE이 DYNAMIC일 경우 minScore")
            .optional(),
        subsectionWithPath(prefix + ".file").description("문제에 해당하는 파일 정보").optional()
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateChallengeDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".challengeId").description("해당 문제의 Id"),
        fieldWithPath(prefix + ".title").description("문제 제목"),
        fieldWithPath(prefix + ".content").description("문제 설명"),
        fieldWithPath(prefix + ".category.id").description("문제가 속한 카테고리의 id"),
        fieldWithPath(prefix + ".category.name").description("문제가 속한 카테고리의 이름"),
        fieldWithPath(prefix + ".score").description("문제의 점수"),
        fieldWithPath(prefix + ".creatorName").description("문제 생성자 이름"),
        fieldWithPath(prefix + ".contestId").description("문제의 대회 Id"),
        fieldWithPath(prefix + ".solvedTeamCount").description("푼 팀 수"),
        subsectionWithPath(prefix + ".file").description("문제에 해당하는 파일 정보").optional()
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateFlagDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".isCorrect").description("맞췄는지 여부"),
        fieldWithPath(prefix + ".content").description("제출한 Flag")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateCtfSubmitLogDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("해당 로그의 Id"),
        fieldWithPath(prefix + ".submitTime").description("flag 제출 시간"),
        subsectionWithPath(prefix + ".team").description("flag 제출자의 팀"),
        subsectionWithPath(prefix + ".submitter").description("flag 제출자"),
        subsectionWithPath(prefix + ".challenge").description("제출 문제"),
        fieldWithPath(prefix + ".flagSubmitted").description("제출한 flag"),
        fieldWithPath(prefix + ".isCorrect").description("제출 후 맞췄는 지 여부")
    ));
    if (type.equals(ResponseType.PAGE)) {
      commonFields.addAll(Arrays.asList(
          fieldWithPath("page.empty").description("페이지가 비었는 지 여부"),
          fieldWithPath("page.first").description("첫 페이지 인지"),
          fieldWithPath("page.last").description("마지막 페이지 인지"),
          fieldWithPath("page.number").description("요소를 가져 온 페이지 번호 (0부터 시작)"),
          fieldWithPath("page.numberOfElements").description("요소 개수"),
          subsectionWithPath("page.pageable").description("해당 페이지에 대한 DB 정보"),
          fieldWithPath("page.size").description("요청한 페이지 크기"),
          subsectionWithPath("page.sort").description("정렬에 대한 정보"),
          fieldWithPath("page.totalElements").description("총 요소 개수"),
          fieldWithPath("page.totalPages").description("총 페이지")));
    }
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateFileDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("해당 파일의 Id"),
        fieldWithPath(prefix + ".fileName").description("파일 제출 시간"),
        fieldWithPath(prefix + ".filePath").description("파일 경로"),
        fieldWithPath(prefix + ".fileSize").description("파일 크기"),
        fieldWithPath(prefix + ".uploadTime").description("파일 등록 시간"),
        fieldWithPath(prefix + ".ipAddress").description("파일 등록 IP")
    ));
    if (type.equals(ResponseType.PAGE)) {
      commonFields.addAll(Arrays.asList(
          fieldWithPath("page.empty").description("페이지가 비었는 지 여부"),
          fieldWithPath("page.first").description("첫 페이지 인지"),
          fieldWithPath("page.last").description("마지막 페이지 인지"),
          fieldWithPath("page.number").description("요소를 가져 온 페이지 번호 (0부터 시작)"),
          fieldWithPath("page.numberOfElements").description("요소 개수"),
          subsectionWithPath("page.pageable").description("해당 페이지에 대한 DB 정보"),
          fieldWithPath("page.size").description("요청한 페이지 크기"),
          subsectionWithPath("page.sort").description("정렬에 대한 정보"),
          fieldWithPath("page.totalElements").description("총 요소 개수"),
          fieldWithPath("page.totalPages").description("총 페이지")));
    }
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateTeamDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("team Id"),
        fieldWithPath(prefix + ".name").description("team 이름"),
        fieldWithPath(prefix + ".description").description("team 설명"),
        fieldWithPath(prefix + ".score").description("team score")
    ));
    if (type.equals(ResponseType.PAGE)) {
      commonFields.addAll(Arrays.asList(
          fieldWithPath("page.empty").description("페이지가 비었는 지 여부"),
          fieldWithPath("page.first").description("첫 페이지 인지"),
          fieldWithPath("page.last").description("마지막 페이지 인지"),
          fieldWithPath("page.number").description("요소를 가져 온 페이지 번호 (0부터 시작)"),
          fieldWithPath("page.numberOfElements").description("요소 개수"),
          subsectionWithPath("page.pageable").description("해당 페이지에 대한 DB 정보"),
          fieldWithPath("page.size").description("요청한 페이지 크기"),
          subsectionWithPath("page.sort").description("정렬에 대한 정보"),
          fieldWithPath("page.totalElements").description("총 요소 개수"),
          fieldWithPath("page.totalPages").description("총 페이지")));
    }
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateTeamDetailDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".registerTime").description("team 등록 시간"),
        fieldWithPath(prefix + ".creatorId").description("team 생성자 Id"),
        fieldWithPath(prefix + ".contestId").description("team이 속한 contest Id")
    ));
    commonFields.addAll(generateTeamDtoResponseFields(type, success, code, msg));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateTeamHasMemberDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".teamName").description("team 이름"),
        fieldWithPath(prefix + ".memberNickname").description("member 닉네임")
    ));

    if (type.equals(ResponseType.PAGE)) {
      commonFields.addAll(Arrays.asList(
          fieldWithPath("page.empty").description("페이지가 비었는 지 여부"),
          fieldWithPath("page.first").description("첫 페이지 인지"),
          fieldWithPath("page.last").description("마지막 페이지 인지"),
          fieldWithPath("page.number").description("요소를 가져 온 페이지 번호 (0부터 시작)"),
          fieldWithPath("page.numberOfElements").description("요소 개수"),
          subsectionWithPath("page.pageable").description("해당 페이지에 대한 DB 정보"),
          fieldWithPath("page.size").description("요청한 페이지 크기"),
          subsectionWithPath("page.sort").description("정렬에 대한 정보"),
          fieldWithPath("page.totalElements").description("총 요소 개수"),
          fieldWithPath("page.totalPages").description("총 페이지")));
    }
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }


  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }
}
