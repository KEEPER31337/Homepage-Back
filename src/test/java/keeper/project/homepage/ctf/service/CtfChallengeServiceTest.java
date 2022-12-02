package keeper.project.homepage.ctf.service;

import static java.time.LocalDateTime.now;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회장;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.우수회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.FORENSIC;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.DYNAMIC;
import static keeper.project.homepage.ctf.service.CtfChallengeService.RETRY_SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.ctf.controller.CtfSpringTestHelper;
import keeper.project.homepage.ctf.dto.CtfFlagDto;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.ctf.entity.CtfTeamEntity;
import keeper.project.homepage.ctf.exception.CustomSubmitCountNotEnoughException;
import keeper.project.homepage.ctf.exception.CustomTooFastRetryException;
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
    LocalDateTime beforeSubmit = now();
    CtfFlagEntity flagEntity = generateCtfFlag(teamEntity, dynamicChallenge, false);
    Long probId = dynamicChallenge.getId();
    CtfFlagDto submitFlag = generateFlag(flagEntity.getContent());

    // when
    CtfFlagDto result = ctfChallengeService.checkFlag(probId, submitFlag);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSolvedTime()).isNotNull();
    assertThat(result.getSolvedTime()).isBefore(now());
    assertThat(result.getSolvedTime()).isAfter(beforeSubmit);
    assertThat(result.getIsCorrect()).isTrue();
  }

  @Test
  @DisplayName("플래그 체크 - 맞췄을 때 제출 팀의 마지막 문제 푼 시간이 갱신되는지 테스트")
  void checkFlag_success_lastSolvedTime() {
    // given
    LocalDateTime beforeSubmit = now();
    Long probId = dynamicChallenge.getId();
    CtfFlagEntity flagEntity = generateCtfFlag(teamEntity, dynamicChallenge, false);
    CtfFlagDto submitFlag = generateFlag(flagEntity.getContent());
    checkInitLastSolveTimeIsBeforeThanNow();

    // when
    CtfFlagDto result = ctfChallengeService.checkFlag(probId, submitFlag);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSolvedTime()).isNotNull();
    assertThat(result.getSolvedTime()).isBefore(now());
    assertThat(result.getSolvedTime()).isAfter(beforeSubmit);
    assertThat(result.getIsCorrect()).isTrue();
    assertThat(teamEntity.getLastSolveTime()).isBefore(now());
    assertThat(teamEntity.getLastSolveTime()).isAfter(beforeSubmit);
  }

  @Test
  @DisplayName("플래그 체크 - 남은 제출 횟수가 1 이상일 때 제출 팀의 제출 횟수가 차감되는지 확인")
  void checkFlag_success_isDecreaseRemainedSubmitCount() {
    // given
    long remainedSubmitCount = 123L;
    CtfFlagEntity flagEntity = generateCtfFlag(teamEntity, dynamicChallenge, false,
        remainedSubmitCount);
    Long probId = dynamicChallenge.getId();
    CtfFlagDto submitFlag = generateFlag(flagEntity.getContent());

    // when
    ctfChallengeService.checkFlag(probId, submitFlag);

    // then
    Long afterRemainedSubmitCount = ctfFlagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
            probId, teamEntity.getId())
        .orElseThrow()
        .getRemainedSubmitCount();
    assertThat(afterRemainedSubmitCount).isNotNull();
    assertThat(afterRemainedSubmitCount).isEqualTo(remainedSubmitCount - 1);
  }

  private void checkInitLastSolveTimeIsBeforeThanNow() {
    assertThat(teamEntity.getLastSolveTime()).isBefore(now());
  }


  @Test
  @DisplayName("플래그 체크 - 남은 제출 횟수가 0 일 때 제출 팀의 제출 횟수가 차감되는지 확인")
  void checkFlag_fail_isDecreaseRemainedSubmitCount0() {
    // given
    long remainedSubmitCount = 0L;
    CtfFlagEntity flagEntity = generateCtfFlag(teamEntity, dynamicChallenge, false,
        remainedSubmitCount);
    Long probId = dynamicChallenge.getId();
    CtfFlagDto submitFlag = generateFlag(flagEntity.getContent());

    // when
    // then
    assertThatThrownBy(() -> ctfChallengeService.checkFlag(probId, submitFlag))
        .isInstanceOf(CustomSubmitCountNotEnoughException.class);
  }


  @Test
  @DisplayName("플래그 체크 - 마지막 제출 시간이 제대로 갱신되는지 확인")
  void checkFlag_success_isUpdateLastTryTime() {
    // given
    CtfFlagEntity flagEntity = generateCtfFlag(teamEntity, dynamicChallenge, false,
        now().minusSeconds(RETRY_SECONDS));
    Long probId = dynamicChallenge.getId();
    CtfFlagDto submitFlag = generateFlag(flagEntity.getContent());

    // when
    LocalDateTime before = now();
    ctfChallengeService.checkFlag(probId, submitFlag);
    LocalDateTime lastTryTime = ctfFlagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
            probId, teamEntity.getId())
        .orElseThrow()
        .getLastTryTime()
        .get();
    LocalDateTime after = now();

    // then
    assertThat(lastTryTime).isAfter(before);
    assertThat(lastTryTime).isBefore(after);
  }


  @Test
  @DisplayName("플래그 체크 - 마지막 제출 시간보다 허용 재시도 시간이 짧으면 오류 반환")
  void checkFlag_fail_isTooFastRetry() {
    // given
    CtfFlagEntity flagEntity = generateCtfFlag(teamEntity, dynamicChallenge, false,
        now());
    Long probId = dynamicChallenge.getId();
    CtfFlagDto submitFlag = generateFlag(flagEntity.getContent());

    // when
    // then
    assertThatThrownBy(() -> ctfChallengeService.checkFlag(probId, submitFlag))
        .isInstanceOf(CustomTooFastRetryException.class);
  }


  @Test
  @DisplayName("플래그 체크 - 실패")
  void checkFlag_fail() {
    // given
    Long probId = dynamicChallenge.getId();
    CtfFlagEntity flagEntity = generateCtfFlag(teamEntity, dynamicChallenge, false);
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