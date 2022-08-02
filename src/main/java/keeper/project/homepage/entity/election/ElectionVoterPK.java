package keeper.project.homepage.entity.election;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import keeper.project.homepage.entity.member.MemberEntity;

@Embeddable
public class ElectionVoterPK implements Serializable {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "voter_id", nullable = false)
  private MemberEntity voter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "election_id", nullable = false)
  private ElectionEntity election;

}
