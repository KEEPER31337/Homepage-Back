package keeper.project.homepage.ctf.entity;


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

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ctf_challenge_type")
public class CtfChallengeTypeEntity {
  public static final CtfChallengeTypeEntity STANDARD = new CtfChallengeTypeEntity(1L, "STANDARD");
  public static final CtfChallengeTypeEntity DYNAMIC = new CtfChallengeTypeEntity(2L, "DYNAMIC");

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, length = 45)
  String name;
}
