package keeper.project.homepage.repository;

import java.util.Optional;
import keeper.project.homepage.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  Optional<MemberEntity> findByLoginId(String loginId);

  boolean existsByLoginId(String loginId);

  boolean existsByEmailAddress(String emailAddress);

  boolean existsByStudentId(String studentId);
}
