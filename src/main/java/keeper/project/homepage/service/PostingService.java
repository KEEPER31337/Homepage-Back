package keeper.project.homepage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.MemberHasPostingLikeEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.repository.MemberHasPostingDislikeRepository;
import keeper.project.homepage.repository.MemberHasPostingLikeRepository;
import keeper.project.homepage.repository.PostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostingService {

  private final PostingRepository postingRepository;
  private final MemberHasPostingLikeRepository memberHasPostingLikeRepository;
  private final MemberHasPostingDislikeRepository memberHasPostingDislikeRepository;

  public List<PostingEntity> findAll(Pageable pageable) {
    return postingRepository.findAll(pageable).getContent();
  }

  public List<PostingEntity> findAllByCategoryId(CategoryEntity categoryEntity, Pageable pageable) {
    return postingRepository.findAllByCategoryId(categoryEntity, pageable);
  }

  public PostingEntity save(PostingEntity postingEntity) {
    return postingRepository.save(postingEntity);
  }

  @Transactional
  public PostingEntity getPostingById(Long pid) {
    return postingRepository.findById(pid).get();
  }

  @Transactional
  public PostingEntity updateById(PostingEntity postingEntity, Long postingId) {
    PostingEntity tempEntity = postingRepository.findById(postingId).get();

    tempEntity.updateInfo(postingEntity.getTitle(), postingEntity.getContent(),
        postingEntity.getUpdateTime(), postingEntity.getIpAddress(),
        postingEntity.getAllowComment(), postingEntity.getIsNotice(), postingEntity.getIsSecret());

    return postingRepository.save(tempEntity);
  }

  @Transactional
  public int deleteById(Long postingId) {
    Optional<PostingEntity> postingEntity = postingRepository.findById(postingId);

    if (postingEntity.isPresent()) {
      postingRepository.delete(postingEntity.get());
      return 1;
    } else {
      return 0;
    }
  }

  @Transactional
  public List<PostingEntity> searchPosting(String type, String keyword,
      CategoryEntity categoryEntity, Pageable pageable) {
    List<PostingEntity> postingEntities = new ArrayList<>();
    switch (type) {
      case "T": {
        postingEntities = postingRepository.findAllByCategoryIdAndTitleContaining(categoryEntity,
            keyword, pageable);
        break;
      }
      case "C": {
        postingEntities = postingRepository.findAllByCategoryIdAndContentContaining(categoryEntity,
            keyword, pageable);
        break;
      }
      case "TC": {
        postingEntities = postingRepository.findAllByCategoryIdAndTitleContainingOrContentContaining(
            categoryEntity, keyword, keyword, pageable);
        break;
      }
      case "W": {
        /*
         * 멤버 기능 구현 완료시 추가
         * MemberEntity memberEntity = memberRepository.findByNickname??(keyword);
         * postingEntities = postingRepository.findAllByCategoryIdAndMemberId(categoryEntity, memberEntity, pageable);
         */
        break;
      }
    }
    return postingEntities;
  }

  @Transactional
  public boolean isPostingLike(MemberEntity memberEntity, PostingEntity postingEntity,
      String type) {
    MemberHasPostingLikeEntity memberHasPostingLikeEntity = MemberHasPostingLikeEntity.builder()
        .memberId(memberEntity).postingId(postingEntity).build();
    if (type.equals("INC")) {
      if (postingRepository.existsByMemberHasPostingLikeEntitiesContaining(
          memberHasPostingLikeEntity)) {
        return false;
      } else {
        postingEntity.increaseLikeCount(memberHasPostingLikeEntity);
        postingRepository.save(postingEntity);
        return true;
      }
    } else {
      if (postingRepository.existsByMemberHasPostingLikeEntitiesContaining(
          memberHasPostingLikeEntity)) {
        memberHasPostingLikeRepository.deleteByMemberIdAndPostingId(memberEntity, postingEntity);
        postingEntity.decreaseLikeCount();
        postingRepository.saveAndFlush(postingEntity);
        return true;
      } else {
        return false;
      }
    }
  }

  @Transactional
  public boolean isPostingDislike(MemberEntity memberEntity, PostingEntity postingEntity,
      String type) {
    MemberHasPostingDislikeEntity memberHasPostingDislikeEntity = MemberHasPostingDislikeEntity.builder()
        .memberId(memberEntity).postingId(postingEntity).build();

    if (type.equals("INC")) {
      if (postingRepository.existsByMemberHasPostingDislikeEntitiesContaining(
          memberHasPostingDislikeEntity)) {
        return false;
      } else {
        postingEntity.increaseDislikeCount(memberHasPostingDislikeEntity);
        postingRepository.save(postingEntity);
        return true;
      }
    } else {
      if (postingRepository.existsByMemberHasPostingDislikeEntitiesContaining(
          memberHasPostingDislikeEntity)) {
        memberHasPostingDislikeRepository.deleteByMemberIdAndPostingId(memberEntity, postingEntity);
        postingEntity.decreaseDislikeCount();
        postingRepository.saveAndFlush(postingEntity);
        return true;
      } else {
        return false;
      }
    }
  }
}
