package keeper.project.homepage.service.attendance;

import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RankService {

  private final MemberRepository memberRepository;

  public List<MemberEntity> getRankList(Pageable pageable) {

    return memberRepository.findAll(pageable).getContent();
  }
}
