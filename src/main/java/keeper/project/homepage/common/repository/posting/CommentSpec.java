package keeper.project.homepage.common.repository.posting;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import keeper.project.homepage.common.entity.posting.CommentEntity;
import keeper.project.homepage.common.entity.posting.PostingEntity;
import org.springframework.data.jpa.domain.Specification;

public class CommentSpec {

  public static Specification<CommentEntity> equalParentId(final Long id) {
    return new Specification<CommentEntity>() {
      @Override
      public Predicate toPredicate(Root<CommentEntity> root, CriteriaQuery<?> query,
          CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get("parentId"), id);
      }
    };
  }

  public static Specification<CommentEntity> equalPosting(final PostingEntity posting) {
    return new Specification<CommentEntity>() {
      @Override
      public Predicate toPredicate(Root<CommentEntity> root, CriteriaQuery<?> query,
          CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get("postingId"), posting);
      }
    };
  }
}
