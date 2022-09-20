package keeper.project.homepage.repository.posting;

import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasPostingDislikeEntity;
import keeper.project.homepage.member.entity.MemberHasPostingLikeEntity;
import keeper.project.homepage.posting.entity.CategoryEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<PostingEntity, Long> {

  Page<PostingEntity> findAllByIsTemp(Integer isTemp, Pageable pageable);

  List<PostingEntity> findAllByIsNoticeAndIsTemp(Integer isNotice, Integer isTemp);

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
      MemberEntity member, Integer isTemp, Integer isNotice, Pageable pageable);

  List<PostingEntity> findAllByMemberId(MemberEntity member);

  Page<PostingEntity> findAllByMemberIdAndIsTempAndIsSecret(
      MemberEntity member, Integer isTemp, Integer isSecret, Pageable pageable);

  List<PostingEntity> findAllByIsTempAndIsSecretAndIsNoticeAndRegisterTimeBetween(Integer isTemp,
      Integer isSecret, Integer isNotice, LocalDateTime registerTime, LocalDateTime registerTime2);

  PostingEntity findFirstByCategoryIdAndMemberIdOrderByRegisterTimeDesc(CategoryEntity category,
      MemberEntity memberEntity);

  boolean existsByMemberHasPostingLikeEntitiesContaining(
      MemberHasPostingLikeEntity memberHasPostingLikeEntity);

  boolean existsByMemberHasPostingDislikeEntitiesContaining(
      MemberHasPostingDislikeEntity memberHasPostingDislikeEntity);
}
