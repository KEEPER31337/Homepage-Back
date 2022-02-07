package keeper.project.homepage.repository.member;

import java.util.Optional;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTypeRepository extends JpaRepository<MemberTypeEntity, Long> {

  Optional<MemberTypeEntity> findByName(String name);
}
