package keeper.project.homepage.repository.posting;

import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

  Page<CommentEntity> findAllByPostingId(PostingEntity postingEntity, Pageable pageable);

  Page<CommentEntity> findAllByParentIdAndPostingId(Long parentId, PostingEntity postingEntity,
      Pageable pageable);

  List<CommentEntity> findAllByMemberId(MemberEntity memberEntity);
}
