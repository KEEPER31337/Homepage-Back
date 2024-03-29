package keeper.project.homepage.election.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "election_voter")
public class ElectionVoterEntity {

  @EmbeddedId
  private ElectionVoterPK electionVoterPK;

  @Column(nullable = false)
  private Boolean isVoted;

  public static ElectionVoterEntity createVoter(ElectionVoterPK pk) {
    return ElectionVoterEntity.builder()
        .electionVoterPK(pk)
        .isVoted(false)
        .build();
  }

  public void vote() {
    isVoted = true;
  }
}
