package keeper.project.homepage.entity.posting;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import keeper.project.homepage.dto.posting.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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

  @OneToMany(mappedBy = "parentId")
  private List<CategoryEntity> children = new ArrayList<>();

  public void updateInfo(CategoryDto categoryDto) {
    this.name = categoryDto.getName();
    this.parentId = categoryDto.getParentId();
  }
}