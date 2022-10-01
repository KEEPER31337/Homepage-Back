package keeper.project.homepage.member.repository;

import java.util.Optional;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTypeRepository extends JpaRepository<MemberTypeEntity, Long> {

  Optional<MemberTypeEntity> findByName(String name);
}
