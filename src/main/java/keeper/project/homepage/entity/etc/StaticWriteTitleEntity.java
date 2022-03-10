package keeper.project.homepage.entity.etc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "static_write_title")
public class StaticWriteTitleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50)
  private String title;

  @Column(length = 40)
  private String type;

  @OneToMany(mappedBy = "staticWriteTitle", cascade = CascadeType.REMOVE)
  @Builder.Default
  private List<StaticWriteSubtitleImageEntity> staticWriteSubtitleImages = new ArrayList<>();

  // type명이 바뀌면 혼란스러울 수 있으므로 type명은 안바뀐다고 가정.
  public void updateInfo(String title) {
    this.title = title;
  }
}
