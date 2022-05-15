package keeper.project.homepage.admin.service.ctf;

import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.admin.dto.ctf.CtfContestDto;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.ctf.CustomContestNotFoundException;
import keeper.project.homepage.repository.ctf.CtfContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CtfAdminService {

  private final AuthService authService;
  private final CtfContestRepository ctfContestRepository;

  public CtfContestDto createContest(CtfContestDto contestDto) {
    contestDto.setJoinable(false);
    MemberEntity creator = authService.getMemberEntityWithJWT();
    log.info("??: ", creator);
    return ctfContestRepository.save(contestDto.toEntity(creator)).toDto();
  }

  public CtfContestDto openContest(Long ctfId) {
    CtfContestEntity contestEntity = ctfContestRepository.findById(ctfId)
        .orElseThrow(CustomContestNotFoundException::new);
    contestEntity.setIsJoinable(true);
    return contestEntity.toDto();
  }

  public CtfContestDto closeContest(Long ctfId) {
    CtfContestEntity contestEntity = ctfContestRepository.findById(ctfId)
        .orElseThrow(CustomContestNotFoundException::new);
    contestEntity.setIsJoinable(false);
    return contestEntity.toDto();
  }

  public List<CtfContestDto> getContests() {
    List<CtfContestEntity> contestEntities = ctfContestRepository.findAll();
    return contestEntities.stream().map(CtfContestEntity::toDto).collect(Collectors.toList());
  }
}
