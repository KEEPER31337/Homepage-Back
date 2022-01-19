package keeper.project.homepage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Table;

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
  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.LAZY)
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
  private String password;
  @ManyToOne(targetEntity = CategoryEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  @JsonIgnore
  private CategoryEntity categoryId;
//  @OneToOne(targetEntity = ThumbnailEntity.class, fetch = FetchType.LAZY)
//  @JoinColumn(name = "thumbnail_id")
//  @JsonIgnore
//  private ThumbnailEntity thumbnailId;

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

  public void makeAnonymous() {
    this.writer = null;
  }

  public void setWriter(String writer) {
    this.writer = writer;
  }
}
