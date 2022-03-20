package keeper.project.homepage.entity.etc;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import keeper.project.homepage.admin.dto.etc.StaticWriteSubtitleImageDto;
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
@Table(name = "static_write_subtitle_image")
public class StaticWriteSubtitleImageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 250)
  private String subtitle;

  @Column(name = "display_order", nullable = false)
  private Integer displayOrder;

  @ManyToOne
  @JoinColumn(name = "static_write_title_id", nullable = false)
  private StaticWriteTitleEntity staticWriteTitle;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "thumbnail_id")
  private ThumbnailEntity thumbnail;

  @OneToMany(mappedBy = "staticWriteSubtitleImage", cascade = CascadeType.REMOVE)
  @Builder.Default
  private List<StaticWriteContentEntity> staticWriteContents = new ArrayList<>();

  public void updateInfo(StaticWriteSubtitleImageDto staticWriteSubtitleImageDto,
      StaticWriteTitleEntity staticWriteTitleEntity, ThumbnailEntity thumbnailEntity) {
    this.subtitle = staticWriteSubtitleImageDto.getSubtitle();
    this.displayOrder = staticWriteSubtitleImageDto.getDisplayOrder();
    this.staticWriteTitle = staticWriteTitleEntity;
    this.thumbnail = thumbnailEntity;
  }
}
