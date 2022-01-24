package keeper.project.homepage.repository.member;

import java.util.Optional;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

  Optional<MemberEntity> findByLoginId(String loginId);

  Optional<MemberEntity> findByEmailAddress(String emailAddress);

  boolean existsByLoginId(String loginId);

  boolean existsByEmailAddress(String emailAddress);

  boolean existsByStudentId(String studentId);
}
