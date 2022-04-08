package keeper.project.homepage.entity.etc;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import keeper.project.homepage.admin.dto.etc.StaticWriteContentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "static_write_content")
public class StaticWriteContentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "display_order")
  private Integer displayOrder;

  @ManyToOne
  @JoinColumn(name = "static_write_subtitle_image_id", nullable = false)
  @JsonBackReference
  private StaticWriteSubtitleImageEntity staticWriteSubtitleImage;

  public void updateInfo(StaticWriteContentDto staticWriteContentDto,
      StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity) {
    this.content = staticWriteContentDto.getContent();
    this.displayOrder = staticWriteContentDto.getDisplayOrder();
    this.staticWriteSubtitleImage = staticWriteSubtitleImageEntity;
  }
}
