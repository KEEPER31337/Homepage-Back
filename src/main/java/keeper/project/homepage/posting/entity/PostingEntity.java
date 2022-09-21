package keeper.project.homepage.posting.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.member.entity.MemberHasPostingDislikeEntity;
import keeper.project.homepage.member.entity.MemberHasPostingLikeEntity;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Setter;
import org.springframework.util.Assert;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posting")
public class PostingEntity {

  @Id // pk
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String title;
  @Column
  private String content;
  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.EAGER)
  // 한명의 유저는 여러개의 게시글 작성, 게시글 작성은 한명이므로 1 : N 관계
  @JoinColumn(name = "member_id") // foreign key 매핑
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private MemberEntity memberId;
  // DB 테이블과 별도의 변수 - Transient Annotation
  @Column
  private Integer visitCount;
  @Column
  private Integer likeCount;
  @Column
  private Integer dislikeCount;
  @Column
  private Integer commentCount;
  @Column
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime registerTime;
  @Column
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime updateTime;
  @Column
  private String ipAddress;
  @Column
  private Integer allowComment;
  @Column
  private Integer isNotice;
  @Column
  private Integer isSecret;
  @Column
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;
  @Column
  private Integer isTemp;
  @ManyToOne(targetEntity = CategoryEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private CategoryEntity categoryId;
  @ManyToOne
  @JoinColumn(name = "thumbnail_id")
  @Setter
  private ThumbnailEntity thumbnail;
  @OneToMany(cascade = CascadeType.ALL, targetEntity = MemberHasPostingLikeEntity.class, mappedBy = "postingId", orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Builder.Default
  private List<MemberHasPostingLikeEntity> memberHasPostingLikeEntities = new ArrayList<>();

  @OneToMany(mappedBy = "postingId")
  @Builder.Default
  private List<FileEntity> files = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, targetEntity = MemberHasPostingDislikeEntity.class, mappedBy = "postingId", orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Builder.Default
  private List<MemberHasPostingDislikeEntity> memberHasPostingDislikeEntities = new ArrayList<>();

  public void updateInfo(String title, String content, LocalDateTime updateTime, String ipAddress,
      Integer allowComment, Integer isNotice, Integer isSecret) {
    this.title = title;
    this.content = content;
    this.updateTime = updateTime;
    this.ipAddress = ipAddress;
    this.allowComment = allowComment;
    this.isNotice = isNotice;
    this.isSecret = isSecret;
  }

  public void updateMemberId(MemberEntity memberId) {
    this.memberId = memberId;
  }

  public void increaseCommentCount() {
    Assert.isTrue(this.commentCount < Integer.MAX_VALUE, "comment_count value will be overflow.");
    this.commentCount += 1;
  }

  public void decreaseCommentCount() {
    Assert.isTrue(this.commentCount >= 0, "comment_count value will be underflow.");
    this.commentCount -= 1;
  }

  public void increaseVisitCount() {
    Assert.isTrue(this.visitCount < Integer.MAX_VALUE, "like_count value will be overflow.");
    this.visitCount += 1;
  }

  public void increaseLikeCount(MemberHasPostingLikeEntity memberHasPostingLikeEntity) {
    Assert.isTrue(this.likeCount < Integer.MAX_VALUE, "like_count value will be overflow.");
    this.likeCount += 1;
    if (this.memberHasPostingLikeEntities == null) {
      this.memberHasPostingLikeEntities = new ArrayList<>();
    }
    this.memberHasPostingLikeEntities.add(memberHasPostingLikeEntity);
  }

  public void decreaseLikeCount() {
    Assert.isTrue(this.likeCount > 0, "like_count mush be bigger than zero");
    this.likeCount -= 1;
  }

  public void increaseDislikeCount(MemberHasPostingDislikeEntity memberHasPostingDislikeEntity) {
    Assert.isTrue(this.dislikeCount < Integer.MAX_VALUE, "dislike_count value will be overflow.");
    this.dislikeCount += 1;
    if (this.memberHasPostingDislikeEntities == null) {
      this.memberHasPostingDislikeEntities = new ArrayList<>();
    }
    this.memberHasPostingDislikeEntities.add(memberHasPostingDislikeEntity);
  }

  public void decreaseDislikeCount() {
    Assert.isTrue(this.dislikeCount > 0, "dislike_count mush be bigger than zero");
    this.dislikeCount -= 1;
  }
}
