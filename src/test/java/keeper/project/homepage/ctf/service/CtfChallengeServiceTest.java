package keeper.project.homepage.ctf.service;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회장;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.FORENSIC;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.DYNAMIC;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.ctf.controller.CtfSpringTestHelper;
import keeper.project.homepage.ctf.dto.CtfFlagDto;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CtfChallengeServiceTest extends CtfSpringTestHelper {

  @Autowired
  CtfChallengeService ctfChallengeService;

  private CtfTeamEntity teamEntity;
  private CtfFlagEntity flagEntity;
  private CtfChallengeEntity dynamicChallenge;

  @BeforeEach
  void setCtfChallenge() {
    MemberEntity adminEntity = generateMemberEntity(회장, 정회원, 우수회원);
    MemberEntity userEntity = generateMemberEntity(회원, 정회원, 일반회원);
    CtfContestEntity contest = generateCtfContest(adminEntity, true);
    Long score = 1000L;
    Long maxScore = 1234L;
    Long minScore = 567L;
    dynamicChallenge = generateCtfChallenge(contest, DYNAMIC, FORENSIC, score, true);
    generateDynamicChallengeInfo(dynamicChallenge, maxScore, minScore);
    teamEntity = generateCtfTeam(contest, userEntity, 0L);
    flagEntity = generateCtfFlag(teamEntity, dynamicChallenge, false);
    setAuthentication(userEntity, "ROLE_회원");
  }

  private static void setAuthentication(MemberEntity userEntity, String role) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(userEntity.getId(), userEntity.getPassword(),
            List.of(new SimpleGrantedAuthority(role))));
  }

  @Test
  @DisplayName("플래그 체크 - 맞춤")
  void checkFlag_success() {
    // given
    LocalDateTime beforeSubmit = LocalDateTime.now();
    Long probId = dynamicChallenge.getId();
    CtfFlagDto submitFlag = generateFlag(flagEntity.getContent());

    // when
    CtfFlagDto result = ctfChallengeService.checkFlag(probId, submitFlag);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSolvedTime()).isNotNull();
    assertThat(result.getSolvedTime()).isBefore(LocalDateTime.now());
    assertThat(result.getSolvedTime()).isAfter(beforeSubmit);
    assertThat(result.getIsCorrect()).isTrue();
  }

  @Test
  @DisplayName("플래그 체크 - 실패")
  void checkFlag_fail() {
    // given
    Long probId = dynamicChallenge.getId();
    CtfFlagDto submitFlag = generateFlag(flagEntity.getContent() + "wrong!");

    // when
    CtfFlagDto result = ctfChallengeService.checkFlag(probId, submitFlag);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSolvedTime()).isNull();
    assertThat(result.getIsCorrect()).isFalse();
  }

  private CtfFlagDto generateFlag(String content) {
    return CtfFlagDto.builder()
        .content(content)
        .build();
  }
}