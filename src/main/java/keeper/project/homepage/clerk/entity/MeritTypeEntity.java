package keeper.project.homepage.clerk.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "merit_type")
public class MeritTypeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer merit;

  private Boolean isMerit;

  @Column(length = 45)
  private String detail;

  public static MeritTypeEntity newInstance( Integer merit, Boolean isMerit,
      String detail) {
    return MeritTypeEntity.builder()
        .merit(merit)
        .isMerit(isMerit)
        .detail(detail)
        .build();
  }
}
