package keeper.project.homepage.service.util;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.repository.member.MemberHasMemberJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberHasMemberJobRepository hasMemberJobRepository;

  public List<String> getAuthByJWT() {
    List<String> roles = new ArrayList<>();

    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long memberId = Long.valueOf(authentication.getName());
    List<MemberHasMemberJobEntity> memberJobs = hasMemberJobRepository.findAllByMemberEntity_Id(
        memberId);
    for (MemberHasMemberJobEntity memberJob : memberJobs) {
      roles.add(memberJob.getMemberJobEntity().getName());
    }
    return roles;
  }

  public Long getMemberIdByJWT() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return Long.valueOf(authentication.getName());
  }
}
