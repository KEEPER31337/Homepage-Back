package keeper.project.homepage.dto;

import java.lang.reflect.Member;
import java.time.LocalDate;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

  public String content;
  public LocalDate registerTime;
  public LocalDate updateTime;
  public String ipAddress;
  public Integer likeCount;
  public Integer dislikeCount;
  public Long parentId;
  public Long memberId;
  public Long postingId;

  //memberId, parentId는 나중에
  public CommentEntity toEntity(PostingEntity postingEntity, MemberEntity memberEntity) {
    return CommentEntity.builder()
        .content(this.content).registerTime(this.registerTime).updateTime(this.updateTime)
        .ipAddress(this.ipAddress).likeCount(this.likeCount).dislikeCount(this.dislikeCount)
        .parentId(this.parentId).memberId(memberEntity).postingId(postingEntity).build();
  }
}
