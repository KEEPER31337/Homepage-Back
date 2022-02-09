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
@Setter
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
  //  public Long memberId;
//  public Long postingId;

  public CommentEntity toEntity(PostingEntity postingEntity, MemberEntity memberEntity) {
    return CommentEntity.builder()
        .id(this.id).content(this.content).registerTime(this.registerTime)
        .updateTime(this.updateTime).ipAddress(this.ipAddress).likeCount(this.likeCount)
        .dislikeCount(this.dislikeCount).parentId(this.parentId).memberId(memberEntity)
        .postingId(postingEntity).build();
  }
}
