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
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ctf_challenge_type")
public class CtfChallengeTypeEntity {

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, length = 45)
  String name;

  @Getter
  @RequiredArgsConstructor
  public enum CtfChallengeType {
    STANDARD(1L, "STANDARD"),
    DYNAMIC(2L, "DYNAMIC");

    private final Long id;
    private final String name;
  }
}
