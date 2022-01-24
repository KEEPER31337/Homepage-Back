package keeper.project.homepage.entity;

import com.sun.istack.NotNull;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "comment")
public class CommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @NotNull
  private Long id;
  @Column(columnDefinition = "LONGTEXT") //update에서는 동작하지 않아서 create-drop으로 적용해야 한다.
  @NotNull
  private String content;
  @Column
  @NotNull
  private LocalDate registerTime;
  @Column
  @NotNull
  private LocalDate updateTime;
  @Column(length = 128)
  @NotNull
  private String ipAddress;
  @Column
  @NotNull
  private Integer likeCount;
  @Column
  @NotNull
  private Integer dislikeCount;
  @NotNull
  @Column
  private Long parentId;
  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  @NotNull
//  @Column
  private MemberEntity memberId;
  @ManyToOne(targetEntity = PostingEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "posting_id")
  @NotNull
  private PostingEntity postingId;

  public void increaseLikeCount() {
    Assert.isTrue(this.likeCount < Integer.MAX_VALUE, "like_count value will be overflow.");
    this.likeCount += 1;
  }

  public void decreaseLikeCount() {
    Assert.isTrue(this.likeCount > 0, "like_count mush be bigger than zero");
    this.likeCount -= 1;
  }

  public void increaseDislikeCount() {
    Assert.isTrue(this.dislikeCount < Integer.MAX_VALUE, "dislike_count value will be overflow.");
    this.dislikeCount += 1;
  }

  public void decreaseDislikeCount() {
    Assert.isTrue(this.dislikeCount > 0, "dislike_count mush be bigger than zero");
    this.dislikeCount -= 1;
  }

  public void changeProperties(CommentEntity commentEntity) {
    Assert.hasText(commentEntity.content, "content must not be empty");
    Assert.notNull(commentEntity.updateTime, "update_time must not be empty");
    Assert.hasText(commentEntity.ipAddress, "ip_address must not be empty");
    Assert.notNull(commentEntity.likeCount, "like_count must not be empty");
    Assert.notNull(commentEntity.dislikeCount, "dislike_count must not be empty");

    this.content = commentEntity.content;
    this.updateTime = commentEntity.updateTime;
    this.ipAddress = commentEntity.ipAddress;
    this.likeCount = commentEntity.likeCount;
    this.dislikeCount = commentEntity.dislikeCount;
  }
}
