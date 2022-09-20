package keeper.project.homepage.repository.study;

import java.util.List;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.study.entity.StudyEntity;
import keeper.project.homepage.study.entity.StudyHasMemberEntity;
import keeper.project.homepage.study.entity.StudyHasMemberEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyHasMemberRepository extends
    JpaRepository<StudyHasMemberEntity, StudyHasMemberEntityPK> {

  List<StudyHasMemberEntity> findAllByStudyId(Long study_id);

  void deleteByMember(MemberEntity memberEntity);

  Long countByStudy(StudyEntity studyEntity);
}
