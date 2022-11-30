package keeper.project.homepage.ctf.service;

import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회장;
import static keeper.project.homepage.ApiControllerTestHelper.MemberRankName.일반회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity.CtfChallengeCategory.WEB;
import static keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity.CtfChallengeType.STANDARD;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import keeper.project.homepage.ctf.controller.CtfSpringTestHelper;
import keeper.project.homepage.ctf.dto.CtfChallengeAdminDto;
import keeper.project.homepage.ctf.dto.CtfChallengeCategoryDto;
import keeper.project.homepage.ctf.dto.CtfChallengeTypeDto;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.util.service.CtfUtilService;
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
class CtfAdminServiceTest extends CtfSpringTestHelper {

  @Autowired
  private CtfAdminService ctfAdminService;

  private CtfContestEntity ctfContestEntity;

  @BeforeEach
  void setUp() {
    MemberEntity contestCreator = generateMemberEntity(회장, 정회원, 일반회원);
    setAuthentication(contestCreator, 회장);
    ctfContestEntity = generateCtfContest(contestCreator);
  }

  private static void setAuthentication(MemberEntity contestCreator, MemberJobName memberJob) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(contestCreator.getId(),
            contestCreator.getPassword(),
            List.of(new SimpleGrantedAuthority(memberJob.getJobName()))));
  }

  @Test
  @DisplayName("문제 생성 테스트")
  void createChallenge() {
    CtfChallengeAdminDto challengeAdminDto = CtfChallengeAdminDto.builder()
        .isSolvable(true)
        .flag("flag")
        .remainedSubmitCount(123L)
        .type(getStandardType())
        .dynamicInfo(null)
        .content("content")
        .title("title")
        .score(1234L)
        .category(getWebCategory())
        .contestId(ctfContestEntity.getId())
        .build();

    CtfChallengeAdminDto result = ctfAdminService.createChallenge(challengeAdminDto);
    CtfFlagEntity flag = ctfFlagRepository.findByCtfChallengeEntityIdAndCtfTeamEntityId(
        result.getChallengeId(), CtfUtilService.VIRTUAL_TEAM_ID).orElseThrow();

    assertThat(result.getFlag()).isEqualTo("flag");
    assertThat(result.getRemainedSubmitCount()).isEqualTo(123L);
    assertThat(result.getType().getId()).isEqualTo(getStandardType().getId());
    assertThat(result.getDynamicInfo().getMaxScore()).isNull();
    assertThat(result.getDynamicInfo().getMinScore()).isNull();
    assertThat(result.getContent()).isEqualTo("content");
    assertThat(result.getTitle()).isEqualTo("title");
    assertThat(result.getScore()).isEqualTo(1234L);
    assertThat(result.getCategory().getId()).isEqualTo(getWebCategory().getId());
    assertThat(result.getContestId()).isEqualTo(ctfContestEntity.getId());
    assertThat(flag.getRemainedSubmitCount()).isEqualTo(123L);
    assertThat(flag.getIsCorrect()).isEqualTo(false);


  }

  private static CtfChallengeCategoryDto getWebCategory() {
    return CtfChallengeCategoryDto.builder()
        .id(WEB.getId())
        .name(WEB.getName())
        .build();
  }

  private static CtfChallengeTypeDto getStandardType() {
    return CtfChallengeTypeDto.builder()
        .id(STANDARD.getId())
        .name(STANDARD.getName())
        .build();
  }
}