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
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
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

  public PostingResponseDto(PostingEntity postingEntity, Integer size, boolean isOne) {

    ThumbnailEntity postingThumbnail = postingEntity.getThumbnail();
    this.id = postingEntity.getId();
    this.title = postingEntity.getTitle();
    this.content = isOne ? postingEntity.getContent() : "";
    this.writer = postingEntity.getMemberId().getNickName();
    this.writerId = postingEntity.getMemberId().getId();
    this.size = size;
    this.visitCount = postingEntity.getVisitCount();
    this.likeCount = postingEntity.getLikeCount();
    this.dislikeCount = postingEntity.getDislikeCount();
    this.commentCount = postingEntity.getCommentCount();
    this.registerTime = postingEntity.getRegisterTime();
    this.updateTime = postingEntity.getUpdateTime();
    this.ipAddress = postingEntity.getIpAddress();
    this.allowComment = postingEntity.getAllowComment();
    this.isNotice = postingEntity.getIsNotice();
    this.isSecret = postingEntity.getIsSecret();
    this.isTemp = postingEntity.getIsTemp();
    this.category = postingEntity.getCategoryId().getName();
    this.categoryId = postingEntity.getCategoryId().getId();
    this.files = isOne ? postingEntity.getFiles() : null;
    this.thumbnailPath = null;

    // 썸네일 경로 처리
    this.writerThumbnailPath = postingEntity.getMemberId().getThumbnailPath();
    this.thumbnailPath = postingThumbnail == null ?
        EnvironmentProperty.getThumbnailPath(ThumbType.PostThumbnail.getDefaultThumbnailId())
        : EnvironmentProperty.getThumbnailPath(postingThumbnail.getId());

    // 익명게시판 처리
    if (postingEntity.getCategoryId().getName().equals("익명게시판")) {
      this.writer = "익명";
      this.writerId = -1L;
      this.writerThumbnailPath = null;
    }
  }
}
