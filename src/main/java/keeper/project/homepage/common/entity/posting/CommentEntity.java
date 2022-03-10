package keeper.project.homepage.common.entity.posting;

import com.sun.istack.NotNull;
import java.time.LocalDateTime;
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
  private LocalDateTime registerTime;
  @Column
  @NotNull
  private LocalDateTime updateTime;
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
  private MemberEntity member;
  @ManyToOne(targetEntity = PostingEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "posting_id")
  @NotNull
  private PostingEntity postingId;

  public void increaseLikeCount() {
    this.likeCount += 1;
  }

  public void decreaseLikeCount() {
    this.likeCount -= 1;
  }

  public void increaseDislikeCount() {
    this.dislikeCount += 1;
  }

  public void decreaseDislikeCount() {
    this.dislikeCount -= 1;
  }

  public void changeContent(String content) {
    this.content = content;
  }

  public void changeUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
  }

  public void changeMemberId(MemberEntity member) {
    this.member = member;
  }

  public void overwriteInfo(MemberEntity virtual, String initContent) {
    this.member = virtual;
    this.content = initContent;
    this.likeCount = 0;
    this.dislikeCount = 0;
  }
}
