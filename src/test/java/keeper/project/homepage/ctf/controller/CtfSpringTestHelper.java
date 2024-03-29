package keeper.project.homepage.ctf.controller;

import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.DYNAMIC;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.STANDARD;
import static keeper.project.homepage.ctf.service.CtfChallengeService.RETRY_SECONDS;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.ctf.dto.CtfChallengeAdminDto;
import keeper.project.homepage.ctf.dto.CtfChallengeCategoryDto;
import keeper.project.homepage.ctf.dto.CtfChallengeDto;
import keeper.project.homepage.ctf.dto.CtfChallengeTypeDto;
import keeper.project.homepage.ctf.dto.CtfCommonChallengeDto;
import keeper.project.homepage.ctf.dto.CtfDynamicChallengeInfoDto;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeHasCtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfDynamicChallengeInfoEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfSubmitLogEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.ctf.entity.CtfTeamHasMemberEntity;
import keeper.project.homepage.ctf.repository.CtfChallengeCategoryRepository;
import keeper.project.homepage.ctf.repository.CtfChallengeHasCtfChallengeCategoryRepository;
import keeper.project.homepage.ctf.repository.CtfChallengeRepository;
import keeper.project.homepage.ctf.repository.CtfChallengeTypeRepository;
import keeper.project.homepage.ctf.repository.CtfContestRepository;
import keeper.project.homepage.ctf.repository.CtfDynamicChallengeInfoRepository;
import keeper.project.homepage.ctf.repository.CtfFlagRepository;
import keeper.project.homepage.ctf.repository.CtfSubmitLogRepository;
import keeper.project.homepage.ctf.repository.CtfTeamHasMemberRepository;
import keeper.project.homepage.ctf.repository.CtfTeamRepository;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.util.service.CtfUtilService;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.FieldDescriptor;

public class CtfSpringTestHelper extends ApiControllerTestHelper {

  @Autowired
  protected CtfChallengeCategoryRepository ctfChallengeCategoryRepository;

  @Autowired
  protected CtfChallengeHasCtfChallengeCategoryRepository ctfChallengeHasCtfChallengeCategoryRepository;

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
        .isJoinable(true)
        .build();
    ctfContestRepository.save(entity);
    return entity;
  }

  protected CtfFlagEntity generateCtfFlag(CtfTeamEntity ctfTeam, CtfChallengeEntity ctfChallenge,
      Boolean isCorrect, Long remainedSubmitCount, LocalDateTime lastTryTime) {
    final long epochTime = System.nanoTime();
    CtfFlagEntity entity = CtfFlagEntity.builder()
        .content("flag_" + epochTime)
        .ctfTeamEntity(ctfTeam)
        .ctfChallengeEntity(ctfChallenge)
        .isCorrect(isCorrect)
        .remainedSubmitCount(remainedSubmitCount)
        .lastTryTime(lastTryTime)
        .build();
    ctfFlagRepository.save(entity);
    ctfChallenge.getCtfFlagEntity().add(entity);
    return entity;
  }

  protected CtfFlagEntity generateCtfFlag(CtfTeamEntity ctfTeam, CtfChallengeEntity ctfChallenge,
      Boolean isCorrect) {
    return generateCtfFlag(ctfTeam, ctfChallenge, isCorrect, 123L,
        LocalDateTime.now().minusDays(1));
  }

  protected CtfFlagEntity generateCtfFlag(CtfTeamEntity ctfTeam, CtfChallengeEntity ctfChallenge,
      Boolean isCorrect, Long remainedSubmitCount) {
    return generateCtfFlag(ctfTeam, ctfChallenge, isCorrect, remainedSubmitCount,
        LocalDateTime.now().minusDays(1));
  }

  protected CtfFlagEntity generateCtfFlag(CtfTeamEntity ctfTeam, CtfChallengeEntity ctfChallenge,
      Boolean isCorrect, LocalDateTime lastTryTime) {
    return generateCtfFlag(ctfTeam, ctfChallenge, isCorrect, 123L, lastTryTime);
  }

  protected CtfSubmitLogEntity generateCtfSubmitLog(CtfTeamEntity ctfTeam, MemberEntity submitter,
      CtfChallengeEntity ctfChallengeEntity, String submitFlag) {
    final long epochTime = System.nanoTime();
    CtfSubmitLogEntity entity = CtfSubmitLogEntity.builder()
        .submitTime(LocalDateTime.now())
        .teamName(ctfTeam.getName())
        .submitterLoginId(submitter.getLoginId())
        .submitterRealname(submitter.getRealName())
        .challengeName(ctfChallengeEntity.getName())
        .contestName(ctfTeam.getCtfContestEntity().getName())
        .flagSubmitted(submitFlag)
        .isCorrect(false)
        .contest(ctfChallengeEntity.getCtfContestEntity())
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
        .lastSolveTime(LocalDateTime.now())
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
      CtfChallengeType ctfChallengeType,
      List<CtfChallengeCategory> category,
      Long score,
      boolean isSolvable) {
    final long epochTime = System.nanoTime();
    CtfChallengeTypeEntity ctfChallengeTypeEntity = ctfChallengeTypeRepository.getById(
        ctfChallengeType.getId());

    CtfChallengeEntity entity = CtfChallengeEntity.builder()
        .name("name_" + epochTime)
        .description("desc_" + epochTime)
        .registerTime(LocalDateTime.now())
        .creator(memberRepository.getById(1L)) // Virtual Member
        .isSolvable(isSolvable)
        .ctfChallengeTypeEntity(ctfChallengeTypeEntity)
        .ctfChallengeHasCtfChallengeCategoryList(new ArrayList<>())
        .score(score)
        .ctfContestEntity(ctfContestEntity)
        .ctfFlagEntity(new ArrayList<>())
        .maxSubmitCount(100L)
        .build();
    ctfChallengeRepository.save(entity);

    List<CtfChallengeCategoryEntity> ctfChallengeCategoryEntityList = category
        .stream()
        .map(ctfChallengeCategory -> CtfChallengeCategoryEntity
            .builder()
            .id(ctfChallengeCategory.getId())
            .name(ctfChallengeCategory.getName())
            .build())
        .toList();

    for (CtfChallengeCategoryEntity ctfChallengeCategory : ctfChallengeCategoryEntityList) {
      CtfChallengeHasCtfChallengeCategoryEntity save = ctfChallengeHasCtfChallengeCategoryRepository.save(
          CtfChallengeHasCtfChallengeCategoryEntity.builder()
              .challenge(entity)
              .category(ctfChallengeCategory)
              .build());
      entity.getCtfChallengeHasCtfChallengeCategoryList().add(save);
    }

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
    ctfChallengeEntity.setScore(maxScore);
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
        subsectionWithPath(prefix + ".creator").description("생성자의 정보가 담겨 나갑니다.")
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
        fieldWithPath(prefix + ".categories[].id").description("문제가 속한 카테고리의 id"),
        fieldWithPath(prefix + ".categories[].name").description("문제가 속한 카테고리의 이름"),
        fieldWithPath(prefix + ".score").description("문제의 점수"),
        fieldWithPath(prefix + ".isSolved").description("내가 풀었는 지"),
        fieldWithPath(prefix + ".maxSubmitCount").description("최대 제출 횟수"),
        fieldWithPath(prefix + ".remainedSubmitCount").description("남은 제출 횟수"),
        fieldWithPath(prefix + ".lastTryTime").description("각 팀별 마지막 제출 시간입니다. 만약 " + RETRY_SECONDS
                + "초 내에 다시 시도할 경우 프론트에서 API 호출을 막아주는게 좋습니다. \n\n"
                + "해당 필드가 만들어지기 전의 문제들은 null값을 보냅니다.")
            .optional(),
        fieldWithPath(prefix + ".solvedTime").description(
                "각 팀별 문제를 해결한 시간입니다. 만약 해결하지 않았다면 null 값을 보냅니다. \n\n"
                    + "해당 필드가 만들어지기 전의 문제들은 해결이 되었어도 null값을 보냅니다.")
            .optional(),
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
        fieldWithPath(prefix + ".categories[].id").description("문제가 속한 카테고리의 id"),
        fieldWithPath(prefix + ".categories[].name").description("문제가 속한 카테고리의 이름"),
        fieldWithPath(prefix + ".type.id").description("문제가 속한 타입의 id"),
        fieldWithPath(prefix + ".type.name").description("문제가 속한 타입의 이름"),
        fieldWithPath(prefix + ".flag").description("문제에 설정 된 flag (현재는 모든 팀이 동일한 flag를 가집니다."),
        fieldWithPath(prefix + ".score").description("문제의 점수"),
        fieldWithPath(prefix + ".creatorName").description("문제 생성자 이름"),
        fieldWithPath(prefix + ".contestId").description("문제의 대회 Id"),
        fieldWithPath(prefix + ".registerTime").description("문제의 등록 시간"),
        fieldWithPath(prefix + ".isSolvable").description("현재 풀 수 있는 지 여부"),
        fieldWithPath(prefix + ".maxSubmitCount").description("최대 제출 횟수"),
        fieldWithPath(prefix + ".remainedSubmitCount").description("남은 제출 횟수"),
        fieldWithPath(prefix + ".lastTryTime").description("각 팀별 마지막 제출 시간입니다. 만약 " + RETRY_SECONDS
                + "초 내에 다시 시도할 경우 프론트에서 API 호출을 막아주는게 좋습니다. \n\n"
                + "해당 필드가 만들어지기 전의 문제들은 null값을 보냅니다.")
            .optional(),
        fieldWithPath(prefix + ".solvedTime").description(
                "각 팀별 문제를 해결한 시간입니다. 만약 해결하지 않았다면 null 값을 보냅니다. \n\n"
                    + "해당 필드가 만들어지기 전의 문제들은 해결이 되었어도 null값을 보냅니다.")
            .optional(),
        fieldWithPath(prefix + ".isSolved").description("관리자 권한의 isSolved는 항상 null로 주어집니다.")
            .optional(),
        fieldWithPath(prefix + ".solvedTeamCount").description("문제를 푼 팀의 수"),
        subsectionWithPath(prefix + ".dynamicInfo").description("TYPE이 STANDARD일 경우 null")
            .optional(),
        subsectionWithPath(prefix + ".file").description("문제에 해당하는 파일 정보").optional()
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

  protected List<FieldDescriptor> generateChallengeDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".challengeId").description("해당 문제의 Id"),
        fieldWithPath(prefix + ".title").description("문제 제목"),
        fieldWithPath(prefix + ".content").description("문제 설명"),
        fieldWithPath(prefix + ".categories[].id").description("문제가 속한 카테고리의 id"),
        fieldWithPath(prefix + ".categories[].name").description("문제가 속한 카테고리의 이름"),
        fieldWithPath(prefix + ".score").description("문제의 점수"),
        fieldWithPath(prefix + ".creatorName").description("문제 생성자 이름"),
        fieldWithPath(prefix + ".contestId").description("문제의 대회 Id"),
        fieldWithPath(prefix + ".solvedTeamCount").description("푼 팀 수"),
        fieldWithPath(prefix + ".isSolved").description("내가 풀었는 지"),
        fieldWithPath(prefix + ".maxSubmitCount").description("최대 제출 횟수"),
        fieldWithPath(prefix + ".remainedSubmitCount").description("남은 제출 횟수"),
        fieldWithPath(prefix + ".lastTryTime").description("각 팀별 마지막 제출 시간입니다. 만약 " + RETRY_SECONDS
                + "초 내에 다시 시도할 경우 프론트에서 API 호출을 막아주는게 좋습니다. \n\n"
                + "해당 필드가 만들어지기 전의 문제들은 null값을 보냅니다.")
            .optional(),
        fieldWithPath(prefix + ".solvedTime").description(
                "각 팀별 문제를 해결한 시간입니다. 만약 해결하지 않았다면 null 값을 보냅니다. \n\n"
                    + "해당 필드가 만들어지기 전의 문제들은 해결이 되었어도 null값을 보냅니다.")
            .optional(),
        subsectionWithPath(prefix + ".file").description("문제에 해당하는 파일 정보").optional()
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

  protected List<FieldDescriptor> generateFlagDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".isCorrect").description("맞췄는지 여부"),
        fieldWithPath(prefix + ".content").description("제출한 Flag"),
        fieldWithPath(prefix + ".solvedTime").description("문제를 맞춘 시간 (못맞췄다면 null을 반환합니다)")
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
        fieldWithPath(prefix + ".flagSubmitted").description("제출한 flag"),
        fieldWithPath(prefix + ".isCorrect").description("제출 후 맞췄는 지 여부"),
        fieldWithPath(prefix + ".teamName").description("제출 팀 이름"),
        fieldWithPath(prefix + ".submitterLoginId").description("제출자의 login Id"),
        fieldWithPath(prefix + ".submitterRealname").description("제출자의 실제 이름"),
        fieldWithPath(prefix + ".challengeName").description("해당 문제의 이름"),
        fieldWithPath(prefix + ".contestName").description("해당 contest의 이름"),
        fieldWithPath(prefix + ".contestId").description("해당 contest의 Id")
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
        fieldWithPath(prefix + ".score").description("team score"),
        fieldWithPath(prefix + ".lastSolvedTime").description("마지막으로 푼 문제 시간")
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

  protected List<FieldDescriptor> generateRankingDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(List.of(
        fieldWithPath(prefix + ".rank").description("team 랭킹")
    ));
    commonFields.addAll(generateTeamDtoResponseFields(type, success, code, msg));
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
        fieldWithPath(prefix + ".contestId").description("team이 속한 contest Id"),
        subsectionWithPath(prefix + ".teamMembers").description("team에 속한 팀원 정보"),
        subsectionWithPath(prefix + ".solvedChallengeList").description(
            "team이 푼 문제들 정보 (푼 문제이므로 남은 제출 횟수가 0으로 표기되어 나갑니다.)")
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


  protected CtfChallengeAdminDto generateChallengeAdminDto(long score, String flag, String content,
      String title, long maxSubmitCount, long contestId,
      List<CtfChallengeCategoryDto> categoryDtos, CtfChallengeTypeDto type) {
    return generateChallengeAdminDto(score, flag, content, title, maxSubmitCount, contestId,
        categoryDtos, type, null);
  }

  protected CtfChallengeAdminDto generateChallengeAdminDto(long score, String flag, String content,
      String title, long maxSubmitCount, long contestId,
      List<CtfChallengeCategoryDto> categoryDtos, CtfChallengeTypeDto type,
      CtfDynamicChallengeInfoDto dynamicScore) {
    return CtfChallengeAdminDto.builder()
        .challengeDto(CtfChallengeDto.builder()
            .commonChallengeDto(CtfCommonChallengeDto.builder()
                .title(title)
                .score(score)
                .categories(categoryDtos)
                .contestId(contestId)
                .maxSubmitCount(maxSubmitCount)
                .build()
            )
            .content(content)
            .build()
        )
        .isSolvable(true)
        .flag(flag)
        .type(type)
        .dynamicInfo(dynamicScore)
        .build();
  }

  protected static CtfChallengeTypeDto getStandardType() {
    return CtfChallengeTypeDto.builder()
        .id(STANDARD.getId())
        .name(STANDARD.getName())
        .build();
  }

  protected static CtfChallengeTypeDto getDynamicType() {
    return CtfChallengeTypeDto.builder()
        .id(DYNAMIC.getId())
        .name(DYNAMIC.getName())
        .build();
  }

  @AfterAll
  public static void clearFiles() {
    deleteTestFiles();
  }
}
