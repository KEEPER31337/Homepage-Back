package keeper.project.homepage.user.dto.posting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class CommentDto {

  private Long id;
  private String content;
  private LocalDateTime registerTime;
  private LocalDateTime updateTime;
  private String ipAddress;
  private Integer likeCount;
  private Integer dislikeCount;
  private Long parentId;
  private String writer;
  private Long writerId;
  private Long writerThumbnailId;
  private Boolean checkedLike;
  private Boolean checkedDislike;

  public void initWithEntity(CommentEntity commentEntity) {
    this.id = commentEntity.getId();
    this.content = commentEntity.getContent();
    this.registerTime = commentEntity.getRegisterTime();
    this.updateTime = commentEntity.getUpdateTime();
    this.ipAddress = commentEntity.getIpAddress();
    this.likeCount = commentEntity.getLikeCount();
    this.dislikeCount = commentEntity.getDislikeCount();
    this.parentId = commentEntity.getParentId();
    setWriterInfo(commentEntity);
  }

  public void setCheckedLike(boolean checkedLike) {
    this.checkedLike = checkedLike;
  }

  public void setCheckedDislike(boolean checkedDislike) {
    this.checkedDislike = checkedDislike;
  }

  private void setWriterInfo(CommentEntity commentEntity) {
    MemberEntity member = commentEntity.getMember();
    if (member != null) {
      this.writer = member.getNickName();
      this.writerId = member.getId();
      ThumbnailEntity thumbnail = member.getThumbnail();
      if (thumbnail != null) {
        this.writerThumbnailId = thumbnail.getId();
      }
    }
  }
}
