package keeper.project.homepage.dto.posting;

import java.time.LocalDate;
import keeper.project.homepage.entity.posting.CommentEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

  public Long id;
  public String content;
  public LocalDate registerTime;
  public LocalDate updateTime;
  public String ipAddress;
  public Integer likeCount;
  public Integer dislikeCount;
  public Long parentId;

  public void initWithEntity(CommentEntity commentEntity) {
    this.id = commentEntity.getId();
    this.content = commentEntity.getContent();
    this.registerTime = commentEntity.getRegisterTime();
    this.updateTime = commentEntity.getUpdateTime();
    this.ipAddress = commentEntity.getIpAddress();
    this.likeCount = commentEntity.getLikeCount();
    this.dislikeCount = commentEntity.getDislikeCount();
    this.parentId = commentEntity.getParentId();
  }
}
