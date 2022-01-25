package keeper.project.homepage.service.posting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasPostingLikeEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.repository.member.MemberHasPostingDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasPostingLikeRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.posting.PostingRepository;
import keeper.project.homepage.repository.ThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostingService {

  private final PostingRepository postingRepository;
  private final CategoryRepository categoryRepository;
  private final MemberRepository memberRepository;
  private final ThumbnailRepository thumbnailRepository;
  private final MemberHasPostingLikeRepository memberHasPostingLikeRepository;
  private final MemberHasPostingDislikeRepository memberHasPostingDislikeRepository;

  public List<PostingEntity> findAll(Pageable pageable) {
    List<PostingEntity> postingEntities = postingRepository.findAll(pageable).getContent();

    for (PostingEntity postingEntity : postingEntities) {
      postingEntity.setWriter(postingEntity.getMemberId().getNickName());
    }
    return postingEntities;
  }

  public List<PostingEntity> findAllByCategoryId(Long categoryId, Pageable pageable) {

    Optional<CategoryEntity> categoryEntity = categoryRepository.findById(Long.valueOf(categoryId));
    List<PostingEntity> postingEntities = postingRepository.findAllByCategoryId(
        categoryEntity.get(), pageable);

    for (PostingEntity postingEntity : postingEntities) {
      postingEntity.setWriter(postingEntity.getMemberId().getNickName());
    }
    /* 이후 처리할 code
     * if (익명게시판 카테고리 id == categoryId) {
     *  postingEntities.forEach(postingEntity -> postingEntity.makeAnonymous());
     * }
     */

    return postingEntities;
  }

  public PostingEntity save(PostingDto dto) {

    Optional<CategoryEntity> categoryEntity = categoryRepository.findById(
        Long.valueOf(dto.getCategoryId()));
    Optional<MemberEntity> memberEntity = memberRepository.findById(
        Long.valueOf(dto.getMemberId()));
    Optional<ThumbnailEntity> thumbnailEntity = thumbnailRepository.findById(dto.getThumbnailId());
    dto.setRegisterTime(new Date());
    dto.setUpdateTime(new Date());
    PostingEntity postingEntity = dto.toEntity(categoryEntity.get(), memberEntity.get(),
        thumbnailEntity.get());

    return postingRepository.save(postingEntity);
  }

  @Transactional
  public PostingEntity getPostingById(Long pid) {

    PostingEntity postingEntity = postingRepository.findById(pid).get();
    postingEntity.setWriter(postingEntity.getMemberId().getNickName());

    return postingEntity;
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
      Long categoryId, Pageable pageable) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId).get();
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
  public boolean isPostingLike(Long memberId, Long postingId,
      String type) {

    MemberEntity memberEntity = memberRepository.findById(memberId).get();
    PostingEntity postingEntity = postingRepository.findById(postingId).get();
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
  public boolean isPostingDislike(Long memberId, Long postingId,
      String type) {

    MemberEntity memberEntity = memberRepository.findById(memberId).get();
    PostingEntity postingEntity = postingRepository.findById(postingId).get();
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
