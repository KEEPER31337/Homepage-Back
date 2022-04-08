package keeper.project.homepage.user.dto.posting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.util.EnvironmentProperty;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class PostingResponseDto {

  private Long id;
  private String title;
  private String content;
  private String writer;
  private Long writerId;
  private String writerThumbnailPath;
  private Integer size;
  private Integer visitCount;
  private Integer likeCount;
  private Integer dislikeCount;
  private Integer commentCount;
  private LocalDateTime registerTime;
  private LocalDateTime updateTime;
  private String ipAddress;
  private Integer allowComment;
  private Integer isNotice;
  private Integer isSecret;
  private Integer isTemp;
  private String category;
  private Long categoryId;
  private String thumbnailPath;
  private List<FileEntity> files;

  public PostingResponseDto initWithEntity(PostingEntity postingEntity, Integer size, boolean isOne) {

    ThumbnailEntity memberThumbnail = postingEntity.getMemberId().getThumbnail();
    ThumbnailEntity postingThumbnail = postingEntity.getThumbnail();

    PostingResponseDto postingResponseDto = PostingResponseDto.builder()
        .id(postingEntity.getId())
        .title(postingEntity.getTitle())
        .content(isOne ? postingEntity.getContent() : "")
        .writer(postingEntity.getMemberId().getNickName())
        .writerId(postingEntity.getMemberId().getId())
        .writerThumbnailPath(null)
        .size(size)
        .visitCount(postingEntity.getVisitCount())
        .likeCount(postingEntity.getLikeCount())
        .dislikeCount(postingEntity.getDislikeCount())
        .commentCount(postingEntity.getCommentCount())
        .registerTime(postingEntity.getRegisterTime())
        .updateTime(postingEntity.getUpdateTime())
        .ipAddress(postingEntity.getIpAddress())
        .allowComment(postingEntity.getAllowComment())
        .isNotice(postingEntity.getIsNotice())
        .isSecret(postingEntity.getIsSecret())
        .isTemp(postingEntity.getIsTemp())
        .category(postingEntity.getCategoryId().getName())
        .categoryId(postingEntity.getCategoryId().getId())
        .files(isOne ? postingEntity.getFiles() : null)
        .thumbnailPath(null)
        .build();

    // 썸네일 경로 처리
    postingResponseDto.setWriterThumbnailPath(postingEntity.getMemberId().getThumbnailPath());
    postingResponseDto.setThumbnailPath(
        postingThumbnail == null ?
            EnvironmentProperty.getThumbnailPath(DefaultThumbnailInfo.ThumbPosting.getThumbnailId())
            : EnvironmentProperty.getThumbnailPath(postingThumbnail.getId())
    );

    // 익명게시판 처리
    if (postingEntity.getCategoryId().getName().equals("익명게시판")) {
      postingResponseDto.setWriter("익명");
      postingResponseDto.setWriterId(-1L);
      postingResponseDto.setWriterThumbnailPath("");
    }

    return postingResponseDto;
  }
}
