package keeper.project.homepage.repository.posting;

import java.util.List;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasPostingLikeEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<PostingEntity, Long> {

  Page<PostingEntity> findAllByIsTemp(Integer isTemp, Pageable pageable);

  Page<PostingEntity> findAllByCategoryIdAndIsTemp(CategoryEntity category, Integer isTemp,
      Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndIsTempAndIsNotice(CategoryEntity category, Integer isTemp, Integer isNotice);

  Page<PostingEntity> findAllByCategoryIdAndTitleContainingAndIsTemp(CategoryEntity category,
      String title, Integer isTemp, Pageable pageable);

  Page<PostingEntity> findAllByCategoryIdAndContentContainingAndIsTemp(CategoryEntity category,
      String content, Integer isTemp, Pageable pageable);

  Page<PostingEntity> findAllByCategoryIdAndTitleContainingOrCategoryIdAndContentContainingAndIsTemp(
      CategoryEntity category1, String title, CategoryEntity category2, String content,
      Integer isTemp,
      Pageable pageable);

  Page<PostingEntity> findAllByCategoryIdAndMemberIdAndIsTemp(CategoryEntity category,
      MemberEntity member, Integer isTemp, Pageable pageable);

  List<PostingEntity> findAllByMemberId(MemberEntity member);

  boolean existsByMemberHasPostingLikeEntitiesContaining(
      MemberHasPostingLikeEntity memberHasPostingLikeEntity);

  boolean existsByMemberHasPostingDislikeEntitiesContaining(
      MemberHasPostingDislikeEntity memberHasPostingDislikeEntity);
}
