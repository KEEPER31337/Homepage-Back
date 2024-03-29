package keeper.project.homepage.util.service.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.repository.MemberHasMemberJobRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final MemberHasMemberJobRepository hasMemberJobRepository;

  public List<String> getAuthByJWT() {
    List<String> roles = new ArrayList<>();

    Long memberId = getMemberIdByJWT();
    List<MemberHasMemberJobEntity> memberJobs = hasMemberJobRepository.findAllByMemberEntity_Id(
        memberId);
    for (MemberHasMemberJobEntity memberJob : memberJobs) {
      roles.add(memberJob.getMemberJobEntity().getName());
    }
    return roles;
  }

  public Long getMemberIdByJWT() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    long memberId;
    try {
      memberId = Long.parseLong(authentication.getName());
    } catch (Exception e) {
      throw new AccessDeniedException("");
    }
    return memberId;
  }

  public MemberEntity getMemberEntityWithJWT() {
    Long memberId = getMemberIdByJWT();
    Optional<MemberEntity> member = memberRepository.findById(memberId);
    if (member.isEmpty()) {
      throw new AccessDeniedException("");
    }
    return member.get();
  }
}
