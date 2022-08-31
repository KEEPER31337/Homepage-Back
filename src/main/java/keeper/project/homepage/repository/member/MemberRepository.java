package keeper.project.homepage.repository.member;

import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  Optional<MemberEntity> findByLoginId(String loginId);

  Optional<MemberEntity> findByEmailAddress(String emailAddress);

  Optional<MemberEntity> findByNickName(String nickName);

  Page<MemberEntity> findAllByIdIsNot(Long id, Pageable pageable);

  List<MemberEntity> findAllByMemberTypeOrderByGenerationAsc(MemberTypeEntity memberType);

  List<MemberEntity> findByRealNameContaining(String keyword);

  @Query("SELECT DISTINCT generation FROM MemberEntity WHERE generation IS NOT NULL ORDER BY generation DESC")
  List<Float> findDistinctGenerationOrderByGenerationDesc();

  boolean existsByLoginId(String loginId);

  boolean existsByEmailAddress(String emailAddress);

  boolean existsByStudentId(String studentId);
}