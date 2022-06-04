package keeper.project.homepage.user.service.ctf;

import java.util.List;
import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.repository.ctf.CtfChallengeCategoryRepository;
import keeper.project.homepage.repository.ctf.CtfChallengeTypeRepository;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.user.dto.ctf.CtfChallengeCategoryDto;
import keeper.project.homepage.user.dto.ctf.CtfChallengeTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CtfExtraDataService {

  private final MemberHasMemberJobRepository memberHasMemberJobRepository;
  private final MemberJobRepository memberJobRepository;
  private final CtfChallengeTypeRepository challengeTypeRepository;
  private final CtfChallengeCategoryRepository challengeCategoryRepository;

  public List<CommonMemberDto> getChallengeMakerList() {
    return memberHasMemberJobRepository.findAllByMemberJobEntity(
            memberJobRepository.findByName("ROLE_출제자").orElseThrow(
                () -> new RuntimeException("출제자 권한이 없습니다. 전산 관리자에게 문의하세요.")))
        .stream()
        .map(memberHasMemberJob -> CommonMemberDto.toDto(memberHasMemberJob.getMemberEntity()))
        .toList();

  }

  public List<CtfChallengeTypeDto> getChallengeTypeList() {
    return challengeTypeRepository.findAll().stream().map(CtfChallengeTypeDto::toDto).toList();
  }

  public List<CtfChallengeCategoryDto> getChallengeCategoryList() {
    return challengeCategoryRepository.findAll().stream().map(CtfChallengeCategoryDto::toDto)
        .toList();
  }
}
