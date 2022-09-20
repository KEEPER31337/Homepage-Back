package keeper.project.homepage.election.entity;

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
import javax.persistence.Table;
import keeper.project.homepage.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "election")
public class ElectionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 45)
  private String name;

  @Column(length = 200)
  private String description;

  @Column(nullable = false)
  private LocalDateTime registerTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator", nullable = false)
  private MemberEntity creator;

  @Column(nullable = false)
  private Boolean isAvailable;

  @Builder.Default
  @OneToMany(mappedBy = "election", cascade = CascadeType.REMOVE)
  List<ElectionCandidateEntity> candidates = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "electionVoterPK.election")
  List<ElectionVoterEntity> voters = new ArrayList<>();

  public void openElection() {
    this.isAvailable = true;
  }

  public void closeElection() { this.isAvailable = false; }
}
