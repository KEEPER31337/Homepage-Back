package keeper.project.homepage.service.attendance;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.dto.rank.RankResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RankService {

  private final MemberRepository memberRepository;
  public static final int DEFAULT_SIZE = 2000; // MAXIMUM PAGE SIZE

  public List<RankResult> getRankList(Pageable pageable) {

    if (isGetAll(pageable)) {
      pageable = Pageable.unpaged(); // 모든 정보 불러오기
    }

    return getRankResults(pageable);
  }

  private List<RankResult> getRankResults(Pageable pageable) {
    List<RankResult> results = new ArrayList<>();
    List<MemberEntity> members = memberRepository.findAll(pageable).getContent();
    int rank = 1;
    for (MemberEntity member : members) {
      RankResult rankResult = new RankResult();
      rankResult.initWithEntity(member);
      rankResult.setRank(rank++);
      results.add(rankResult);
    }
    return results;
  }

  private boolean isGetAll(Pageable pageable) {
    return pageable.getPageSize() == DEFAULT_SIZE;
  }
}
