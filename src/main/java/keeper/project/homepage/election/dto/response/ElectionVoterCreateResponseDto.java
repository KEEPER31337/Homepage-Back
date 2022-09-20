package keeper.project.homepage.election.dto.response;

import keeper.project.homepage.election.entity.ElectionVoterEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectionVoterCreateResponseDto {

  private Long electionId;
  private Long voterId;

  public static ElectionVoterCreateResponseDto from(ElectionVoterEntity entity) {
    return ElectionVoterCreateResponseDto.builder()
        .electionId(entity.getElectionVoterPK().getElection().getId())
        .voterId(entity.getElectionVoterPK().getVoter().getId())
        .build();
  }

}
