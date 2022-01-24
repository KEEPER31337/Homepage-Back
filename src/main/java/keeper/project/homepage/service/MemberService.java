package keeper.project.homepage.service;

import java.util.Optional;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  public MemberEntity findById(Long id) throws RuntimeException {
    Optional<MemberEntity> memberEntity = memberRepository.findById(id);
    return memberEntity.orElse(null);
  }
}
