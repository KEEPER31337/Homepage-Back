package keeper.project.homepage.controller.ctf;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.ctf.CtfChallengeCategoryRepository;
import keeper.project.homepage.repository.ctf.CtfChallengeRepository;
import keeper.project.homepage.repository.ctf.CtfChallengeTypeRepository;
import keeper.project.homepage.repository.ctf.CtfContestRepository;
import keeper.project.homepage.repository.ctf.CtfFlagRepository;
import keeper.project.homepage.repository.ctf.CtfSubmitLogRepository;
import keeper.project.homepage.repository.ctf.CtfTeamRepository;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.FieldDescriptor;

public class CtfControllerTestHelper extends ApiControllerTestHelper {

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

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonContent = mapper.writeValueAsString(obj);
      return jsonContent;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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
