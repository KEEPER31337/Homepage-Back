package keeper.project.homepage.repository.study;

import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.study.StudyEntity;
import keeper.project.homepage.entity.study.StudyHasMemberEntity;
import keeper.project.homepage.entity.study.StudyHasMemberEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyHasMemberRepository extends
    JpaRepository<StudyHasMemberEntity, StudyHasMemberEntityPK> {

  List<StudyHasMemberEntity> findAllByStudyId(Long study_id);

  void deleteByMember(MemberEntity memberEntity);

  Long countByStudy(StudyEntity studyEntity);
}
