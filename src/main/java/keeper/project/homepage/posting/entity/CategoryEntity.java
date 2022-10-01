package keeper.project.homepage.posting.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "category")
public class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 250, nullable = false)
  private String name;

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "href", length = 45)
  private String href;

  @OneToMany(mappedBy = "parentId")
  @Builder.Default
  private List<CategoryEntity> children = new ArrayList<>();

}