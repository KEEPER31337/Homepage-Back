package keeper.project.homepage.entity.posting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import keeper.project.homepage.entity.member.MemberHasPostingDislikeEntity;
import keeper.project.homepage.entity.member.MemberHasPostingLikeEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Table;
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
  @Transient
  private String writer;
  @Column
  private Integer visitCount;
  @Column
  private Integer likeCount;
  @Column
  private Integer dislikeCount;
  @Column
  private Integer commentCount;
  @Column
  private Date registerTime;
  @Column
  private Date updateTime;
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
  @OneToOne(targetEntity = ThumbnailEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_id")
  @JsonIgnore
  private ThumbnailEntity thumbnailId;
  @OneToMany(cascade = CascadeType.ALL, targetEntity = MemberHasPostingLikeEntity.class, mappedBy = "postingId", orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Builder.Default
  private List<MemberHasPostingLikeEntity> memberHasPostingLikeEntities = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, targetEntity = MemberHasPostingDislikeEntity.class, mappedBy = "postingId", orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Builder.Default
  private List<MemberHasPostingDislikeEntity> memberHasPostingDislikeEntities = new ArrayList<>();

  public void updateInfo(String title, String content, Date updateTime, String ipAddress,
      Integer allowComment, Integer isNotice, Integer isSecret) {
    this.title = title;
    this.content = content;
    this.updateTime = updateTime;
    this.ipAddress = ipAddress;
    this.allowComment = allowComment;
    this.isNotice = isNotice;
    this.isSecret = isSecret;
  }

  public void increaseVisitCount() {
    Assert.isTrue(this.likeCount < Integer.MAX_VALUE, "like_count value will be overflow.");
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

  public void setWriter(String writer) {
    this.writer = writer;
  }

  public void makeSecret() {
    this.title = "비밀 게시글입니다.";
    this.content = "비밀 게시글입니다.";
  }
}
