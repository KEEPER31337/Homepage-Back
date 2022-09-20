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
import keeper.project.homepage.member.entity.MemberJobEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "election_candidate")
public class ElectionCandidateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private MemberEntity candidate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "election_id", nullable = false)
  private ElectionEntity election;

  @Column(length = 200)
  private String description;

  @Column(nullable = false)
  private LocalDateTime registerTime;

  @Column(nullable = false)
  private Integer voteCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_job_id", nullable = false)
  private MemberJobEntity memberJob;

  @Builder.Default
  @OneToMany(mappedBy = "electionCandidate", cascade = CascadeType.REMOVE)
  List<ElectionChartLogEntity> chartLogs = new ArrayList<>();

  public void gainVote() {
    voteCount += 1;
  }
}
