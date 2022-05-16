package keeper.project.homepage.entity.ctf;

import java.time.LocalDateTime;
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
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ctf_challenge")
public class CtfChallengeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  Long id;

  @Column(length = 200, nullable = false)
  String name;

  @Column(length = 200)
  String description;

  @Column(nullable = false)
  LocalDateTime registerTime;

  @ManyToOne
  @JoinColumn(name = "creator", nullable = false)
  MemberEntity creator;

  @Column(nullable = false)
  @Setter
  Boolean isSolvable;

  @ManyToOne
  @JoinColumn(name = "type_id", nullable = false)
  CtfChallengeTypeEntity ctfChallengeTypeEntity;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  CtfChallengeCategoryEntity ctfChallengeCategoryEntity;

  @Column(nullable = false)
  @Setter
  Long score;

  @ManyToOne
  @JoinColumn(name = "contest_id", nullable = false)
  CtfContestEntity ctfContestEntity;

  @OneToOne
  @JoinColumn(name = "file_id")
  FileEntity fileEntity;

  @OneToMany(
      mappedBy = "ctfChallengeEntity",
      cascade = CascadeType.REMOVE)
  List<CtfFlagEntity> ctfFlagEntity;
}
