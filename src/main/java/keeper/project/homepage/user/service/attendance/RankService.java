package keeper.project.homepage.user.service.attendance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.user.dto.attendance.RankDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RankService {

  private final MemberRepository memberRepository;

  public Page<RankDto> getRankings(Pageable pageable) {
    int rank = 1;
    List<RankDto> rankings = new ArrayList<>();
    List<MemberEntity> members = memberRepository.findAll(pageable).stream()
        .filter(member -> member.getId() != 1).collect(Collectors.toList());

    for (MemberEntity member : members) {
      RankDto rankDto = RankDto.toDto(member);
      rankDto.setRank(rank++);
      rankings.add(rankDto);
    }

    return new PageImpl<>(rankings);
  }
}
