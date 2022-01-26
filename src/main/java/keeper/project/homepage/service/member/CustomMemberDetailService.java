package keeper.project.homepage.service.member;

import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomMemberDetailService implements UserDetailsService {

  private final MemberRepository memberRepository;

  public UserDetails loadUserByUsername(String userPk) {
    return memberRepository.findById(Long.valueOf(userPk))
        .orElseThrow(CustomMemberNotFoundException::new);
  }

}