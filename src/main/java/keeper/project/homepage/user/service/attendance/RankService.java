package keeper.project.homepage.user.service.attendance;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.user.dto.attendance.RankDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RankService {

  private final MemberRepository memberRepository;
  public static final int DEFAULT_SIZE = 2000; // MAXIMUM PAGE SIZE

  public List<RankDto> getRankList(Pageable pageable) {

    if (isGetAll(pageable)) {
      pageable = Pageable.unpaged(); // 모든 정보 불러오기
    }

    return getRankResults(pageable);
  }

  private List<RankDto> getRankResults(Pageable pageable) {
    List<RankDto> results = new ArrayList<>();
    List<MemberEntity> members;
    if (pageable == Pageable.unpaged()) {
      members = memberRepository.findAll(Sort.by(Direction.DESC, "point"));
    } else {
      members = memberRepository.findAll(pageable).getContent();
    }
    int rank = 1;
    for (MemberEntity member : members) {
      if (member.getId() == 1) {
        continue;
      }
      RankDto rankDto = new RankDto();
      rankDto.initWithEntity(member);
      rankDto.setRank(rank++);
      results.add(rankDto);
    }

    return results;
  }

  private boolean isGetAll(Pageable pageable) {
    return pageable.getPageSize() == DEFAULT_SIZE;
  }
}
