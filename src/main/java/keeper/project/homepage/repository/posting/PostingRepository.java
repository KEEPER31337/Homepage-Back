package keeper.project.homepage.repository.posting;

import java.util.List;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasPostingLikeEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<PostingEntity, Long> {

  List<PostingEntity> findAllByCategoryId(CategoryEntity category, Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndTitleContaining(CategoryEntity category, String title,
      Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndContentContaining(CategoryEntity category,
      String content, Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndTitleContainingOrCategoryIdAndContentContaining(
      CategoryEntity category1, String title, CategoryEntity category2, String content,
      Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndMemberId(CategoryEntity category, MemberEntity member,
      Pageable pageable);

  boolean existsByMemberHasPostingLikeEntitiesContaining(
      MemberHasPostingLikeEntity memberHasPostingLikeEntity);

  boolean existsByMemberHasPostingDislikeEntitiesContaining(
      MemberHasPostingDislikeEntity memberHasPostingDislikeEntity);

}
