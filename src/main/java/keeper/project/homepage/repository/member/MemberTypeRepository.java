package keeper.project.homepage.repository.member;

import keeper.project.homepage.entity.member.MemberTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTypeRepository extends JpaRepository<MemberTypeEntity, Long> {

}
