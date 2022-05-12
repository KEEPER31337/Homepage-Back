package keeper.project.homepage.entity.ctf;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ctf_challenge_category")
public class CtfChallengeCategoryEntity {

  @Id
  @Column(nullable = false)
  Long id;

  @Column(nullable = false, length = 45)
  String name;
}
