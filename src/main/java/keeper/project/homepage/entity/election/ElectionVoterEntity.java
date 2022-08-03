package keeper.project.homepage.entity.election;

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

}
