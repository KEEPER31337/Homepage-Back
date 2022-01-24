package keeper.project.homepage.dto;

import java.util.Date;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.PostingEntity;
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
public class PostingDto {

  private Long memberId;
  private Long categoryId;
  private String title;
  private String content;
  private Integer visitCount;
  private Integer likeCount;
  private Integer dislikeCount;
  private Integer commentCount;
  private Date registerTime;
  private Date updateTime;
  private String ipAddress;
  private Integer allowComment;
  private Integer isNotice;
  private Integer isSecret;
  private String password;

  public PostingEntity toEntity(CategoryEntity categoryEntity, MemberEntity memberEntity) {
    this.visitCount = 0;
    this.likeCount = 0;
    this.dislikeCount = 0;
    this.commentCount = 0;

    return PostingEntity.builder().title(title).content(content).visitCount(visitCount)
        .likeCount(likeCount).dislikeCount(dislikeCount).commentCount(commentCount)
        .registerTime(registerTime).updateTime(updateTime).ipAddress(ipAddress)
        .allowComment(allowComment).isNotice(isNotice).isSecret(isSecret).password(password)
        .categoryId(categoryEntity).memberId(memberEntity).build();
  }
}
