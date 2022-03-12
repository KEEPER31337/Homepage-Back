package keeper.project.homepage.user.dto.posting;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.posting.PostingEntity;
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
  private Long userThumbnailID;
  private LocalDateTime dateTime;
  private Integer watch;
  private Integer commentN;
  private Long categoryId;
  private String category;
  private Long ThumbnailId;

  public void initWithEntity(PostingEntity postingEntity) {
    this.id = postingEntity.getId();
    this.title = postingEntity.getTitle();
//    this.user = postingEntity.getWriter();
//    this.userThumbnailID = postingEntity.getWriterThumbnailId();
    this.dateTime = postingEntity.getRegisterTime();
    this.watch = postingEntity.getVisitCount();
    this.commentN = postingEntity.getCommentCount();
    this.categoryId = postingEntity.getCategoryId().getId();
    this.category = postingEntity.getCategoryId().getName();
    if (postingEntity.getThumbnail() != null) {
      this.ThumbnailId = postingEntity.getThumbnail().getId();
    }
  }
}