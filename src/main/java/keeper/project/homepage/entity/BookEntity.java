package keeper.project.homepage.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
  @Column(name = "picture", unique = true, length = 512)
  private String picture;
  @Column(name = "information")
  private String information;
  @Column(name = "total", nullable = false)
  private Long total;
  @Column(name = "borrow", nullable = false)
  private Long borrow;
  @Column(name = "enable", nullable = false)
  private Long enable;
  @Column(name = "register_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date registerDate;

}
