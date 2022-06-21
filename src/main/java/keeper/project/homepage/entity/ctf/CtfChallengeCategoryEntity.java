package keeper.project.homepage.entity.ctf;

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
@Table(name = "ctf_challenge_category")
public class CtfChallengeCategoryEntity {

  public static final CtfChallengeCategoryEntity MISC =
      new CtfChallengeCategoryEntity(1L, "Misc");
  public static final CtfChallengeCategoryEntity SYSTEM =
      new CtfChallengeCategoryEntity(2L, "System");
  public static final CtfChallengeCategoryEntity REVERSING =
      new CtfChallengeCategoryEntity(3L, "Reversing");
  public static final CtfChallengeCategoryEntity FORENSIC =
      new CtfChallengeCategoryEntity(4L, "Forensic");
  public static final CtfChallengeCategoryEntity WEB =
      new CtfChallengeCategoryEntity(5L, "Web");
  public static final CtfChallengeCategoryEntity CRYPTO =
      new CtfChallengeCategoryEntity(6L, "Crypto");

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, length = 45)
  String name;
}
