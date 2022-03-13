package keeper.project.homepage.entity.study;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.ThumbnailEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "study")
public class StudyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 45, nullable = false)
  private String title;

  @Column(length = 256)
  private String information;

  @Column(nullable = false)
  private Integer memberNumber;

  @Column
  private LocalDateTime registerTime;

  @Column
  private Integer year;

  @Column
  private Integer season;

  @Column(length = 256)
  private String gitLink;

  @Column(length = 256)
  private String noteLink;

  @Column(length = 256)
  private String etcLink;

  @OneToOne
  @JoinColumn(name = "thumbnail_id")
  private ThumbnailEntity thumbnail;
}
