package keeper.project.homepage.dto.posting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
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

  private MemberEntity memberId;
  private Long categoryId;
  private String title;
  private String content;
  @Builder.Default
  private Integer visitCount = 0;
  @Builder.Default
  private Integer likeCount = 0;
  @Builder.Default
  private Integer dislikeCount = 0;
  @Builder.Default
  private Integer commentCount = 0;
  private LocalDateTime registerTime;
  private LocalDateTime updateTime;
  private String ipAddress;
  private Integer allowComment;
  private Integer isNotice;
  private Integer isSecret;
  private Integer isTemp;
  private String password;
  private Long thumbnailId;
}
