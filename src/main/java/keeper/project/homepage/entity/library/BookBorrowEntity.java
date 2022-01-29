package keeper.project.homepage.entity.library;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_borrow_info")
public class BookBorrowEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.EAGER)
  // 한명의 유저는 여러개의 게시글 작성, 게시글 작성은 한명이므로 1 : N 관계
  @JoinColumn(name = "member_id") // foreign key 매핑
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private MemberEntity memberId;
  @OneToOne(targetEntity = BookEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")
  @JsonIgnore
  private BookEntity bookId;
  @Column(name = "quantity", nullable = false)
  private Long quantity;
  @Column(name = "borrow_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date borrowDate;
  @Column(name = "expire_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date expireDate;

}
