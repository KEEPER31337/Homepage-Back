package keeper.project.homepage.repository.study;

import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.study.StudyHasMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyHasMemberRepository extends
    JpaRepository<StudyHasMemberEntity, MemberEntity> {


}
