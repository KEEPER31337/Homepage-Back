package keeper.project.homepage.dto;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostingDto {

  private Integer memberId;
  private Integer categoryId;
  private String title;
  private String content;
  private Integer visitCount;
  private Integer likeCount;
  private Integer dislikeCount;
  private Integer commentCount;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime registerTime;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;
  private String ipAddress;
  private Integer allowComment;
  private Integer isNotice;
  private Integer isSecret;
  private String password;

  //Member Entity는 추후 추가
  public PostingEntity toEntity(CategoryEntity categoryEntity) {

    return PostingEntity.builder().title(title).content(content).visitCount(visitCount)
        .likeCount(likeCount).dislikeCount(dislikeCount).commentCount(commentCount)
        .registerTime(Date.from(registerTime.toInstant(ZoneOffset.UTC)))
        .updateTime(Date.from(updateTime.toInstant(ZoneOffset.UTC))).ipAddress(ipAddress)
        .allowComment(allowComment).isNotice(isNotice).isSecret(isSecret).password(password)
        .categoryId(categoryEntity).build();
  }
}
