package keeper.project.homepage.posting.repository;

import java.util.List;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.posting.entity.CommentEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

  List<CommentEntity> findAll(Specification<CommentEntity> spec);

  List<CommentEntity> findAll(Specification<CommentEntity> spec, Pageable pageable);

  List<CommentEntity> findAllByMember(MemberEntity memberEntity);

  List<CommentEntity> findAllByPostingId(PostingEntity postingEntity);

  Long countByMember(MemberEntity memberEntity);
}
