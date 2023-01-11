package keeper.project.homepage.attendance.service;

import keeper.project.homepage.attendance.dto.RankDto;
import keeper.project.homepage.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RankService {

  private final MemberRepository memberRepository;

  public Page<RankDto> getRankings(Pageable pageable) {
    final long WITHDRAWAL_MEMBER = 1L;
    long rank = (long) pageable.getPageNumber() * pageable.getPageSize() + 1;

    Page<RankDto> members = memberRepository.findAllByIdIsNot(WITHDRAWAL_MEMBER, pageable)
        .map(RankDto::toDto);
    for (RankDto member : members) {
      member.setRank(rank++);
    }
    return members;
  }
}
