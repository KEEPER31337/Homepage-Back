package keeper.project.homepage.user.dto.posting;

import java.time.LocalDateTime;
import keeper.project.homepage.common.controller.util.ImageController;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostingBestDto {

  private Long id;
  private String title;
  private String user;
  private String userThumbnailPath;
  private LocalDateTime dateTime;
  private Integer watch;
  private Integer commentN;
  private Long categoryId;
  private String category;
  private String thumbnailPath;

  public void initWithEntity(PostingEntity postingEntity) {
    this.id = postingEntity.getId();
    this.title = postingEntity.getTitle();
    this.user = postingEntity.getMemberId().getNickName();
    this.userThumbnailPath = null;
    this.dateTime = postingEntity.getRegisterTime();
    this.watch = postingEntity.getVisitCount();
    this.commentN = postingEntity.getCommentCount();
    this.categoryId = postingEntity.getCategoryId().getId();
    this.category = postingEntity.getCategoryId().getName();

    //썸네일 경로 처리
    this.thumbnailPath = postingEntity.getThumbnail() == null ?
        EnvironmentProperty.getThumbnailPath(ThumbType.PostThumbnail.getDefaultThumbnailId())
        : EnvironmentProperty.getThumbnailPath(postingEntity.getThumbnail().getId());
    this.userThumbnailPath = postingEntity.getMemberId().getThumbnailPath();

    //익명게시판 처리
    if (postingEntity.getCategoryId().getName().equals("익명게시판")) {
      this.user = "익명";
      this.userThumbnailPath = null;
    }
  }
}
