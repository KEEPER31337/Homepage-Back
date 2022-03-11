package keeper.project.homepage.user.service.posting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.user.dto.posting.LikeAndDislikeDto;
import keeper.project.homepage.user.dto.posting.PostingBestDto;
import keeper.project.homepage.user.dto.posting.PostingDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasPostingLikeEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.exception.posting.CustomCategoryNotFoundException;
import keeper.project.homepage.repository.FileRepository;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.repository.member.MemberHasPostingDislikeRepository;
import keeper.project.homepage.repository.member.MemberHasPostingLikeRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import keeper.project.homepage.repository.posting.PostingRepository;
import keeper.project.homepage.common.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostingService {

  private final PostingRepository postingRepository;
  private final CategoryRepository categoryRepository;
  private final MemberRepository memberRepository;
  private final FileRepository fileRepository;
  private final ThumbnailRepository thumbnailRepository;
  private final MemberHasPostingLikeRepository memberHasPostingLikeRepository;
  private final MemberHasPostingDislikeRepository memberHasPostingDislikeRepository;
  private final AuthService authService;

  public static final Integer isNotTempPosting = 0;
  public static final Integer isTempPosting = 1;
  public static final Integer isNotSecretPosting = 0;
  public static final Integer isSecretPosting = 1;
  public static final Integer isNotNoticePosting = 0;
  public static final Integer isNoticePosting = 1;
  public static final Integer bestPostingCount = 10;

  public List<PostingEntity> findAll(Pageable pageable) {

    Page<PostingEntity> postingEntities = postingRepository.findAllByIsTemp(isNotTempPosting,
        pageable);
    setAllInfo(postingEntities);

    return postingEntities.getContent();
  }

  private void setWriterInfo(PostingEntity postingEntity) {
    if (postingEntity.getCategoryId().getName().equals("비밀게시판")) {
      postingEntity.setWriter("익명");
    } else {
      postingEntity.setWriter(postingEntity.getMemberId().getNickName());
      postingEntity.setWriterId(postingEntity.getMemberId().getId());
      if (postingEntity.getMemberId().getThumbnail() != null) {
        postingEntity.setWriterThumbnailId(postingEntity.getMemberId().getThumbnail().getId());
      }
    }
  }

  private void setAllInfo(List<PostingEntity> postingEntities) {
    setAllInfo(postingEntities, postingEntities.size());
  }

  private void setAllInfo(Page<PostingEntity> postingEntities) {
    setAllInfo(postingEntities.getContent(), (int) postingEntities.getTotalElements());
  }

  private void setAllInfo(List<PostingEntity> postingEntities, Integer totalSize) {
    for (PostingEntity postingEntity : postingEntities) {
      setWriterInfo(postingEntity);
      postingEntity.setSize(totalSize);
      if (postingEntity.getIsSecret() == 1) {
        postingEntity.makeSecret();
      }
      postingEntity.setCategory(postingEntity.getCategoryId().getName());
    }
  }

  public List<PostingEntity> findAllByCategoryId(Long categoryId, Pageable pageable) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(CustomCategoryNotFoundException::new);
    Page<PostingEntity> postingEntities = postingRepository.findAllByCategoryIdAndIsTempAndIsNotice(
        categoryEntity, isNotTempPosting, isNotNoticePosting, pageable);
    setAllInfo(postingEntities);

    return postingEntities.getContent();
  }

  public List<PostingEntity> findAllNoticeByCategoryId(Long categoryId) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(CustomCategoryNotFoundException::new);
    List<PostingEntity> postingEntities = postingRepository.findAllByCategoryIdAndIsTempAndIsNotice(
        categoryEntity, isNotTempPosting, isNoticePosting);
    setAllInfo(postingEntities);

    return postingEntities;
  }

  public List<PostingBestDto> findAllBest() {

    LocalDateTime startDate = LocalDate.now().minusWeeks(2).atStartOfDay();
    LocalDateTime endDate = LocalDate.now().plusDays(1).atStartOfDay();
    List<PostingEntity> postingEntities = postingRepository.findAllByIsTempAndIsSecretAndIsNoticeAndRegisterTimeBetween(
        isNotTempPosting, isNotSecretPosting, isNotNoticePosting, startDate, endDate);
    setAllInfo(postingEntities);

    postingEntities.sort((posting1, posting2) -> {
      Integer posting1Score =
          posting1.getVisitCount() + posting1.getLikeCount() * 2 - posting1.getDislikeCount();
      Integer posting2Score =
          posting2.getVisitCount() + posting2.getLikeCount() * 2 - posting2.getDislikeCount();

      return posting2Score.compareTo(posting1Score);
    });

    List<PostingBestDto> postingBestDtos = new ArrayList<>();
    for (int i = 0; i < Math.min(postingEntities.size(), bestPostingCount); i++) {
      PostingBestDto tempBestDto = new PostingBestDto();
      tempBestDto.initWithEntity(postingEntities.get(i));
      postingBestDtos.add(tempBestDto);
    }

    return postingBestDtos;
  }

  public PostingEntity save(PostingDto dto) {

    Optional<CategoryEntity> categoryEntity = categoryRepository.findById(
        Long.valueOf(dto.getCategoryId()));
    Optional<ThumbnailEntity> thumbnailEntity = thumbnailRepository.findById(dto.getThumbnailId());
    MemberEntity memberEntity = getMemberEntityWithJWT();
    dto.setRegisterTime(LocalDateTime.now());
    dto.setUpdateTime(LocalDateTime.now());
    PostingEntity postingEntity = dto.toEntity(categoryEntity.get(), memberEntity,
        thumbnailEntity.get());

    memberEntity.getPosting().add(postingEntity);
    return postingRepository.save(postingEntity);
  }

  @Transactional
  public PostingEntity getPostingById(Long pid) {

    PostingEntity postingEntity = postingRepository.findById(pid)
        .orElseThrow(RuntimeException::new); // TODO: CustomPostingNotFoundException 만들어주세여~
    setWriterInfo(postingEntity);
    postingEntity.setSize(1);

    return postingEntity;
  }

  @Transactional
  public PostingEntity updateById(PostingDto dto, Long postingId, ThumbnailEntity newThumbnail) {
    PostingEntity tempEntity = postingRepository.findById(postingId).get();

    dto.setUpdateTime(LocalDateTime.now());
    dto.setCommentCount(tempEntity.getCommentCount());
    dto.setLikeCount(tempEntity.getLikeCount());
    dto.setDislikeCount(tempEntity.getDislikeCount());
    dto.setVisitCount(tempEntity.getVisitCount());

    if (tempEntity.getMemberId().getId() != getMemberEntityWithJWT().getId()) {
      throw new RuntimeException("작성자만 수정할 수 있습니다.");
    }

    tempEntity.updateInfo(dto.getTitle(), dto.getContent(),
        dto.getUpdateTime(), dto.getIpAddress(),
        dto.getAllowComment(), dto.getIsNotice(), dto.getIsSecret());
    tempEntity.setThumbnail(newThumbnail);

    return postingRepository.save(tempEntity);
  }

  @Transactional
  public PostingEntity updateInfoById(PostingEntity postingEntity, Long postingId) {
    PostingEntity tempEntity = postingRepository.findById(postingId).get();

    tempEntity.updateInfo(postingEntity.getTitle(), postingEntity.getContent(),
        postingEntity.getUpdateTime(), postingEntity.getIpAddress(),
        postingEntity.getAllowComment(), postingEntity.getIsNotice(), postingEntity.getIsSecret());

    return postingRepository.save(tempEntity);
  }

  @Transactional
  public void delete(PostingEntity postingEntity) {

    MemberEntity memberEntity = memberRepository.findById(
        postingEntity.getMemberId().getId()).orElseThrow(CustomMemberNotFoundException::new);

    if (!memberEntity.getId().equals(getMemberEntityWithJWT().getId())) {
      throw new RuntimeException("작성자만 삭제할 수 있습니다.");
    }

    // Foreign Key로 연결 된 file 제거
    List<FileEntity> fileEntities = fileRepository.findAllByPostingId(postingEntity);
    for (FileEntity fileEntity : fileEntities) {
      fileEntity.setPostingId(null);
      fileRepository.save(fileEntity);
    }

    memberEntity.getPosting().remove(postingEntity);
    postingRepository.delete(postingEntity);
  }

  @Transactional
  public List<PostingEntity> searchPosting(String type, String keyword,
      Long categoryId, Pageable pageable) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId).get();
    Page<PostingEntity> postingEntities = null;
    switch (type) {
      case "T": {
        postingEntities = postingRepository.findAllByCategoryIdAndTitleContainingAndIsTempAndIsNotice(
            categoryEntity, keyword, isNotTempPosting, isNotNoticePosting, pageable);
        break;
      }
      case "C": {
        postingEntities = postingRepository.findAllByCategoryIdAndContentContainingAndIsTempAndIsNotice(
            categoryEntity, keyword, isNotTempPosting, isNotNoticePosting, pageable);
        break;
      }
      case "TC": {
        postingEntities = postingRepository.findAllByCategoryIdAndTitleContainingOrCategoryIdAndContentContainingAndIsTempAndIsNotice(
            categoryEntity, keyword, categoryEntity, keyword, isNotTempPosting, isNotNoticePosting,
            pageable);
        break;
      }
      case "W": {
        Optional<MemberEntity> memberEntity = memberRepository.findByNickName(keyword);
        if (!memberEntity.isPresent()) {
          break;
        }
        postingEntities = postingRepository.findAllByCategoryIdAndMemberIdAndIsTempAndIsNotice(
            categoryEntity,
            memberEntity.get(), isNotTempPosting, isNotNoticePosting, pageable);
        break;
      }
    }
    setAllInfo(postingEntities);

    return postingEntities.getContent();
  }

  @Transactional
  public boolean isPostingLike(Long postingId, String type) {

    MemberEntity memberEntity = getMemberEntityWithJWT();
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
  public boolean isPostingDislike(Long postingId, String type) {

    MemberEntity memberEntity = getMemberEntityWithJWT();
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

  @Transactional
  public LikeAndDislikeDto checkLikeAndDisLike(Long postingId) {

    MemberEntity memberEntity = getMemberEntityWithJWT();
    PostingEntity postingEntity = postingRepository.findById(postingId).get();
    MemberHasPostingDislikeEntity memberHasPostingDislikeEntity = MemberHasPostingDislikeEntity.builder()
        .memberId(memberEntity).postingId(postingEntity).build();
    MemberHasPostingLikeEntity memberHasPostingLikeEntity = MemberHasPostingLikeEntity.builder()
        .memberId(memberEntity).postingId(postingEntity).build();

    List<Boolean> checked = new ArrayList<>();

    if (postingRepository.existsByMemberHasPostingLikeEntitiesContaining(
        memberHasPostingLikeEntity)) {
      checked.add(true);
    } else {
      checked.add(false);
    }
    if (postingRepository.existsByMemberHasPostingDislikeEntitiesContaining(
        memberHasPostingDislikeEntity)) {
      checked.add(true);
    } else {
      checked.add(false);
    }
    LikeAndDislikeDto likeAndDislikeDto = new LikeAndDislikeDto(checked.get(0), checked.get(1));

    return likeAndDislikeDto;
  }

  private MemberEntity getMemberEntityWithJWT() {
    Long memberId = authService.getMemberIdByJWT();
    Optional<MemberEntity> member = memberRepository.findById(memberId);
    if (member.isEmpty()) {
      throw new CustomMemberNotFoundException();
    }
    return member.get();
  }
}
