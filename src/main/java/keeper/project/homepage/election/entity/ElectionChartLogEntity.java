package keeper.project.homepage.election.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "election_chart_log")
public class ElectionChartLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "election_candidate_id", nullable = false)
  private ElectionCandidateEntity electionCandidate;

  @Column(nullable = false)
  private LocalDateTime voteTime;

  public static ElectionChartLogEntity createChartLog(ElectionCandidateEntity candidate) {
    return ElectionChartLogEntity.builder()
        .electionCandidate(candidate)
        .voteTime(LocalDateTime.now())
        .build();
  }
}
