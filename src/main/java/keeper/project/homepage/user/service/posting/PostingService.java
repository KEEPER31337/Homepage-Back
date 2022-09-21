package keeper.project.homepage.user.service.posting;

import static keeper.project.homepage.util.ClientUtil.getUserIP;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.util.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.posting.exception.CustomPostingAccessDeniedException;
import keeper.project.homepage.posting.exception.CustomPostingIncorrectException;
import keeper.project.homepage.posting.exception.CustomPostingNotFoundException;
import keeper.project.homepage.posting.exception.CustomPostingTempException;
import keeper.project.homepage.attendance.repository.AttendanceRepository;
import keeper.project.homepage.member.repository.MemberHasMemberJobRepository;
import keeper.project.homepage.member.repository.MemberJobRepository;
import keeper.project.homepage.posting.repository.CommentRepository;
import keeper.project.homepage.user.dto.posting.LikeAndDislikeDto;
import keeper.project.homepage.user.dto.posting.PostingBestDto;
import keeper.project.homepage.user.dto.posting.PostingDto;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasPostingDislikeEntity;
import keeper.project.homepage.member.entity.MemberHasPostingLikeEntity;
import keeper.project.homepage.posting.entity.CategoryEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.posting.exception.CustomCategoryNotFoundException;
import keeper.project.homepage.util.repository.FileRepository;
import keeper.project.homepage.util.repository.ThumbnailRepository;
import keeper.project.homepage.member.repository.MemberHasPostingDislikeRepository;
import keeper.project.homepage.member.repository.MemberHasPostingLikeRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.posting.repository.CategoryRepository;
import keeper.project.homepage.posting.repository.PostingRepository;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.user.dto.posting.PostingImageUploadResponseDto;
import keeper.project.homepage.user.dto.posting.PostingResponseDto;
import keeper.project.homepage.user.service.member.MemberUtilService;
import keeper.project.homepage.util.image.preprocessing.ImageNoChange;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PostingService {

  private final PostingRepository postingRepository;
  private final CategoryRepository categoryRepository;
  private final MemberRepository memberRepository;
  private final MemberJobRepository memberJobRepository;
  private final MemberHasMemberJobRepository memberHasMemberJobRepository;
  private final CommentRepository commentRepository;
  private final FileRepository fileRepository;
  private final ThumbnailRepository thumbnailRepository;
  private final MemberHasPostingLikeRepository memberHasPostingLikeRepository;
  private final MemberHasPostingDislikeRepository memberHasPostingDislikeRepository;
  private final AttendanceRepository attendanceRepository;
  private final MemberUtilService memberUtilService;
  private final AuthService authService;
  private final ThumbnailService thumbnailService;

  public static final Integer isNotTempPosting = 0;
  public static final Integer isTempPosting = 1;
  public static final Integer isNotSecretPosting = 0;
  public static final Integer isSecretPosting = 1;
  public static final Integer isNotNoticePosting = 0;
  public static final Integer isNoticePosting = 1;
  public static final Integer bestPostingCount = 10;
  public static final Integer EXAM_BOARD_ACCESS_POINT = 20000;
  public static final Integer EXAM_BOARD_ACCESS_COMMENT_COUNT = 5;
  public static final Integer EXAM_BOARD_ACCESS_ATTEND_COUNT = 10;
  public static final Long EXAM_CATEGORY_ID = 1377L;
  public static final Long INFO_CATEGORY_ID = 5125L;
  public static final String EXAM_ACCESS_DENIED_TITLE = "접근할 수 없습니다.";
  public static final String EXAM_ACCESS_DENIED_CONTENT = "공지사항을 확인해 주세요.";

  public List<PostingResponseDto> findAll(Pageable pageable) {

    Page<PostingEntity> postingEntities = postingRepository.findAllByIsTemp(isNotTempPosting,
        pageable);
    List<PostingResponseDto> postingResponseDtos = new ArrayList<>();

    for (PostingEntity postingEntity : postingEntities) {
      postingResponseDtos.add(new PostingResponseDto(postingEntity,
          (int) postingEntities.getTotalElements(), false));
    }

    return postingResponseDtos;
  }

  public List<PostingResponseDto> findAllByCategoryId(Long categoryId, Pageable pageable) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(CustomCategoryNotFoundException::new);
    Page<PostingEntity> postingEntities = postingRepository.findAllByCategoryIdAndIsTempAndIsNotice(
        categoryEntity, isNotTempPosting, isNotNoticePosting, pageable);
    List<PostingResponseDto> postingResponseDtos = new ArrayList<>();

    for (PostingEntity postingEntity : postingEntities) {
      postingResponseDtos.add(new PostingResponseDto(postingEntity,
          (int) postingEntities.getTotalElements(), false));
    }

    return postingResponseDtos;
  }

  public List<PostingResponseDto> findAllNotice() {

    List<PostingEntity> postingEntities = postingRepository.findAllByIsNoticeAndIsTemp(
        isNoticePosting, isNotTempPosting);
    List<PostingResponseDto> postingResponseDtos = new ArrayList<>();

    for (PostingEntity postingEntity : postingEntities) {
      postingResponseDtos.add(new PostingResponseDto(postingEntity,
          postingEntities.size(), false));
    }

    return postingResponseDtos;
  }

  public List<PostingResponseDto> findAllNoticeByCategoryId(Long categoryId) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(CustomCategoryNotFoundException::new);
    List<PostingEntity> postingEntities = postingRepository.findAllByCategoryIdAndIsTempAndIsNotice(
        categoryEntity, isNotTempPosting, isNoticePosting);
    List<PostingResponseDto> postingResponseDtos = new ArrayList<>();

    for (PostingEntity postingEntity : postingEntities) {
      postingResponseDtos.add(new PostingResponseDto(postingEntity,
          postingEntities.size(), false));
    }

    return postingResponseDtos;
  }

  public List<PostingBestDto> findAllBest() {

    LocalDateTime startDate = LocalDate.now().minusWeeks(2).atStartOfDay();
    LocalDateTime endDate = LocalDate.now().plusDays(1).atStartOfDay();
    List<PostingEntity> postingEntities = postingRepository.findAllByIsTempAndIsSecretAndIsNoticeAndRegisterTimeBetween(
        isNotTempPosting, isNotSecretPosting, isNotNoticePosting, startDate, endDate);

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

  public Map<String, Object> findAllByMemberId(Long otherMemberId, Pageable pageable) {
    MemberEntity other = memberUtilService.getById(otherMemberId);

    Page<PostingEntity> postingPage = postingRepository.findAllByMemberIdAndIsTempAndIsSecret(
        other, isNotTempPosting, isNotSecretPosting, pageable);

    Map<String, Object> result = new HashMap<>();
    List<PostingResponseDto> postingList = new ArrayList<>();
    for (PostingEntity posting : postingPage.getContent()) {
      postingList.add(new PostingResponseDto(posting, (int) postingPage.getTotalElements(), false));
    }

    result.put("isLast", postingPage.isLast());
    result.put("content", postingList);

    return result;
  }

  @Transactional
  public PostingEntity save(PostingDto dto) {

    CategoryEntity categoryEntity = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(CustomCategoryNotFoundException::new);
    ThumbnailEntity thumbnailEntity = thumbnailRepository.findById(dto.getThumbnailId())
        .orElseThrow(CustomThumbnailEntityNotFoundException::new);
    MemberEntity memberEntity = authService.getMemberEntityWithJWT();
    dto.setRegisterTime(LocalDateTime.now());
    dto.setUpdateTime(LocalDateTime.now());
    PostingEntity postingEntity = dto.toEntity(categoryEntity, memberEntity,
        thumbnailEntity);

    memberEntity.getPosting().add(postingEntity);
    return postingRepository.save(postingEntity);
  }

  // Crawler에서 사용
  @Transactional
  public PostingEntity autoSave(PostingDto dto) {

    CategoryEntity categoryEntity = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(CustomCategoryNotFoundException::new);
    ThumbnailEntity thumbnailEntity = thumbnailRepository.findById(dto.getThumbnailId())
        .orElseThrow(CustomThumbnailEntityNotFoundException::new);
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_대외부장")
        .orElse(memberJobRepository.findByName("ROLE_회장").get());
    MemberHasMemberJobEntity memberHasMemberJobEntity = memberHasMemberJobRepository.findFirstByMemberJobEntityOrderByIdDesc(
        memberJobEntity);
    MemberEntity memberEntity = memberHasMemberJobEntity.getMemberEntity();
    dto.setRegisterTime(LocalDateTime.now());
    dto.setUpdateTime(LocalDateTime.now());
    PostingEntity postingEntity = dto.toEntity(categoryEntity, memberEntity,
        thumbnailEntity);

    memberEntity.getPosting().add(postingEntity);
    return postingRepository.save(postingEntity);
  }

  @Transactional
  public PostingEntity getPostingById(Long pid) {

    return postingRepository.findById(pid)
        .orElseThrow(CustomPostingNotFoundException::new);
  }

  @Transactional
  public PostingResponseDto getPostingResponseById(Long pid, Long visitMemberId, String password) {

    PostingEntity postingEntity = postingRepository.findById(pid)
        .orElseThrow(CustomPostingNotFoundException::new);

    if (isNotAccessExamBoard(postingEntity)) {
      return createNotAccessDto(postingEntity);
    }
    if (visitMemberId != postingEntity.getMemberId().getId()) {
      if (postingEntity.getIsTemp() == isTempPosting) {
        throw new CustomPostingTempException();
      } else if (postingEntity.getIsSecret() == isSecretPosting) {
        if (!(postingEntity.getPassword().equals(password))) {
          throw new CustomPostingIncorrectException();
        }
      }
      postingEntity.increaseVisitCount();
      updateInfoById(postingEntity, pid);
    }

    return new PostingResponseDto(postingEntity, 1, true);
  }

  @Transactional
  public PostingEntity updateById(PostingDto dto, Long postingId, ThumbnailEntity newThumbnail) {
    PostingEntity tempEntity = postingRepository.findById(postingId)
        .orElseThrow(CustomPostingNotFoundException::new);

    dto.setUpdateTime(LocalDateTime.now());
    dto.setCommentCount(tempEntity.getCommentCount());
    dto.setLikeCount(tempEntity.getLikeCount());
    dto.setDislikeCount(tempEntity.getDislikeCount());
    dto.setVisitCount(tempEntity.getVisitCount());

    if (tempEntity.getMemberId().getId() != authService.getMemberEntityWithJWT().getId()) {
      throw new CustomPostingAccessDeniedException();
    }

    tempEntity.updateInfo(dto.getTitle(), dto.getContent(),
        dto.getUpdateTime(), dto.getIpAddress(),
        dto.getAllowComment(), dto.getIsNotice(), dto.getIsSecret());
    tempEntity.setThumbnail(newThumbnail);

    return postingRepository.save(tempEntity);
  }

  @Transactional
  public void updateInfoById(PostingEntity postingEntity, Long postingId) {
    PostingEntity tempEntity = postingRepository.findById(postingId)
        .orElseThrow(CustomPostingNotFoundException::new);

    tempEntity.updateInfo(postingEntity.getTitle(), postingEntity.getContent(),
        postingEntity.getUpdateTime(), postingEntity.getIpAddress(),
        postingEntity.getAllowComment(), postingEntity.getIsNotice(), postingEntity.getIsSecret());

    postingRepository.save(tempEntity);
  }

  @Transactional
  public void delete(PostingEntity postingEntity) {

    MemberEntity memberEntity = memberRepository.findById(
        postingEntity.getMemberId().getId()).orElseThrow(CustomMemberNotFoundException::new);

    if (!memberEntity.getId().equals(authService.getMemberEntityWithJWT().getId())) {
      throw new CustomPostingAccessDeniedException();
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

  // Crawler에서 사용
  @Transactional
  public PostingEntity getRecentPostingByCategoryId(Long categoryId) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(CustomCategoryNotFoundException::new);
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_대외부장")
        .orElse(memberJobRepository.findByName("ROLE_회장").get());
    MemberHasMemberJobEntity memberHasMemberJobEntity = memberHasMemberJobRepository.
        findFirstByMemberJobEntityOrderByIdDesc(memberJobEntity);
    MemberEntity memberEntity = memberHasMemberJobEntity.getMemberEntity();

    return postingRepository.findFirstByCategoryIdAndMemberIdOrderByRegisterTimeDesc(categoryEntity,
        memberEntity);
  }

  @Transactional
  public List<PostingResponseDto> searchPosting(String type, String keyword,
      Long categoryId, Pageable pageable) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(CustomPostingNotFoundException::new);
    Page<PostingEntity> postingEntities = Page.empty();
    switch (type) {
      case "T" -> postingEntities = postingRepository.findAllByCategoryIdAndTitleContainingAndIsTempAndIsNotice(
          categoryEntity, keyword, isNotTempPosting, isNotNoticePosting, pageable);

      case "C" -> postingEntities = postingRepository.findAllByCategoryIdAndContentContainingAndIsTempAndIsNotice(
          categoryEntity, keyword, isNotTempPosting, isNotNoticePosting, pageable);

      case "TC" -> postingEntities = postingRepository.findAllByCategoryIdAndTitleContainingOrCategoryIdAndContentContainingAndIsTempAndIsNotice(
          categoryEntity, keyword, categoryEntity, keyword, isNotTempPosting, isNotNoticePosting,
          pageable);

      case "W" -> {
        MemberEntity memberEntity = memberRepository.findByNickName(keyword)
            .orElseThrow(CustomMemberNotFoundException::new);
        postingEntities = postingRepository.findAllByCategoryIdAndMemberIdAndIsTempAndIsNotice(
            categoryEntity, memberEntity, isNotTempPosting, isNotNoticePosting, pageable);
      }
    }
    List<PostingResponseDto> postingResponseDtos = new ArrayList<>();

    for (PostingEntity postingEntity : postingEntities) {
      postingResponseDtos.add(new PostingResponseDto(postingEntity,
          (int) postingEntities.getTotalElements(), false));
    }

    return postingResponseDtos;
  }

  @Transactional
  public boolean isPostingLike(Long postingId, String type) {

    MemberEntity memberEntity = authService.getMemberEntityWithJWT();
    PostingEntity postingEntity = postingRepository.findById(postingId)
        .orElseThrow(CustomPostingNotFoundException::new);
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

    MemberEntity memberEntity = authService.getMemberEntityWithJWT();
    PostingEntity postingEntity = postingRepository.findById(postingId)
        .orElseThrow(CustomPostingNotFoundException::new);
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

    MemberEntity memberEntity = authService.getMemberEntityWithJWT();
    PostingEntity postingEntity = postingRepository.findById(postingId)
        .orElseThrow(CustomPostingNotFoundException::new);
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

    return new LikeAndDislikeDto(checked.get(0), checked.get(1));
  }

  @Transactional
  public PostingImageUploadResponseDto uploadPostingImage(
      Long postingId,
      MultipartFile postingImage,
      HttpServletRequest request){

    PostingEntity postingEntity = getPostingById(postingId);

    ThumbnailEntity postingImageEntity = thumbnailService.save(
        ThumbType.PostThumbnail, new ImageNoChange(), postingImage, getUserIP(request), postingEntity);

    return PostingImageUploadResponseDto.from(postingImageEntity);
  }

  public boolean isNotAccessExamBoard(PostingEntity postingEntity) {
    if (!postingEntity.getCategoryId().getId().equals(EXAM_CATEGORY_ID)) {
      return false;
    }
    if (postingEntity.getIsNotice() == 1) {
      return false;
    }
    MemberEntity visitMember = authService.getMemberEntityWithJWT();
    Integer myPoint = visitMember.getPoint();
    Long myCommentCount = commentRepository.countByMember(visitMember);
    Long myAttendCount = attendanceRepository.countByMember(visitMember);

    if (visitMember.getGeneration() < 13) {
      return false;
    }
    if (myPoint >= EXAM_BOARD_ACCESS_POINT &&
        myCommentCount >= EXAM_BOARD_ACCESS_COMMENT_COUNT &&
        myAttendCount >= EXAM_BOARD_ACCESS_ATTEND_COUNT
    ) {
      return false;
    }
    return true;
  }

  public PostingResponseDto createNotAccessDto(PostingEntity postingEntity) {
    PostingResponseDto postingResponseDto = new PostingResponseDto(postingEntity,
        1, true);
    postingResponseDto.setTitle(EXAM_ACCESS_DENIED_TITLE);
    postingResponseDto.setContent(EXAM_ACCESS_DENIED_CONTENT);
    postingResponseDto.setFiles(new ArrayList<>());
    return postingResponseDto;
  }
}
