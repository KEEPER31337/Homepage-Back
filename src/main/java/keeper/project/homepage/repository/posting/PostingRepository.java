package keeper.project.homepage.repository.posting;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasPostingLikeEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<PostingEntity, Long> {

  Page<PostingEntity> findAllByIsTemp(Integer isTemp, Pageable pageable);

  Page<PostingEntity> findAllByCategoryIdAndIsTempAndIsNotice(CategoryEntity category,
      Integer isTemp, Integer isNotice, Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndIsTempAndIsNotice(CategoryEntity category,
      Integer isTemp, Integer isNotice);

  Page<PostingEntity> findAllByCategoryIdAndTitleContainingAndIsTempAndIsNotice(
      CategoryEntity category,
      String title, Integer isTemp, Integer isNotice, Pageable pageable);

  Page<PostingEntity> findAllByCategoryIdAndContentContainingAndIsTempAndIsNotice(
      CategoryEntity category,
      String content, Integer isTemp, Integer isNotice, Pageable pageable);

  Page<PostingEntity> findAllByCategoryIdAndTitleContainingOrCategoryIdAndContentContainingAndIsTempAndIsNotice(
      CategoryEntity category1, String title, CategoryEntity category2, String content,
      Integer isTemp, Integer isNotice, Pageable pageable);

  Page<PostingEntity> findAllByCategoryIdAndMemberIdAndIsTempAndIsNotice(CategoryEntity category,
      MemberEntity member, Integer isTemp,Integer isNotice, Pageable pageable);

  List<PostingEntity> findAllByMemberId(MemberEntity member);

  List<PostingEntity> findAllByIsTempAndIsSecretAndIsNoticeAndRegisterTimeBetween(Integer isTemp,
      Integer isSecret, Integer isNotice, LocalDateTime registerTime, LocalDateTime registerTime2);

  boolean existsByMemberHasPostingLikeEntitiesContaining(
      MemberHasPostingLikeEntity memberHasPostingLikeEntity);

  boolean existsByMemberHasPostingDislikeEntitiesContaining(
      MemberHasPostingDislikeEntity memberHasPostingDislikeEntity);
}
