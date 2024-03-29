package keeper.project.homepage.ctf.entity;

import java.time.LocalDateTime;
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
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.util.entity.FileEntity;
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
  Long id;

  @Column(length = 200, nullable = false)
  String name;

  @Column(columnDefinition = "TEXT")
  String description;

  @Column(nullable = false)
  LocalDateTime registerTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator", nullable = false)
  MemberEntity creator;

  @Column(nullable = false)
  @Setter
  Boolean isSolvable;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", nullable = false)
  CtfChallengeTypeEntity ctfChallengeTypeEntity;

  @Builder.Default
  @OneToMany(mappedBy = "challenge", cascade = CascadeType.REMOVE)
  List<CtfChallengeHasCtfChallengeCategoryEntity> ctfChallengeHasCtfChallengeCategoryList = new ArrayList<>();

  @Column(nullable = false)
  @Setter
  Long score;

  @Column(name = "max_submit_count", nullable = false)
  Long maxSubmitCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contest_id", nullable = false)
  CtfContestEntity ctfContestEntity;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id", nullable = true)
  @Setter
  FileEntity fileEntity;

  @OneToMany(
      mappedBy = "ctfChallengeEntity",
      cascade = CascadeType.REMOVE)
  List<CtfFlagEntity> ctfFlagEntity = new ArrayList<>();

  @OneToOne(
      mappedBy = "ctfChallengeEntity",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY
  )
  @PrimaryKeyJoinColumn
  @Setter
  CtfDynamicChallengeInfoEntity dynamicChallengeInfoEntity;

  public void addCtfChallengeHasCtfChallengeCategory(CtfChallengeHasCtfChallengeCategoryEntity ctfChallengeHasCtfChallengeCategoryEntity) {
    this.getCtfChallengeHasCtfChallengeCategoryList().add(ctfChallengeHasCtfChallengeCategoryEntity);
  }
}
