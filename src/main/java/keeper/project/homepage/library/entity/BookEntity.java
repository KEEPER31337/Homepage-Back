package keeper.project.homepage.library.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
public class BookEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "title", nullable = false, unique = true, length = 250)
  private String title;
  @Column(name = "author", nullable = false, length = 40)
  private String author;
  @Column(name = "information")
  private String information;
  @ManyToOne(targetEntity = BookDepartmentEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "department")
  private BookDepartmentEntity department;
  @Column(name = "total", nullable = false)
  @Setter
  private Long total;
  @Column(name = "borrow", nullable = false)
  @Setter
  private Long borrow;
  @Column(name = "enable", nullable = false)
  @Setter
  private Long enable;
  @Column(name = "register_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date registerDate;
  @ManyToOne(targetEntity = ThumbnailEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_id")
  @JsonIgnore
  private ThumbnailEntity thumbnailId;

}
