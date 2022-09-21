package keeper.project.homepage.election.dto.response;

import keeper.project.homepage.election.entity.ElectionCandidateEntity;
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
public class ElectionCandidateDeleteResponseDto {

  private Long deletedId;
  private Long candidateMemberId;
  private Long electionId;
  private Long memberJobId;

  public static ElectionCandidateDeleteResponseDto from(ElectionCandidateEntity entity) {
    return ElectionCandidateDeleteResponseDto.builder()
        .deletedId(entity.getId())
        .candidateMemberId(entity.getCandidate().getId())
        .electionId(entity.getElection().getId())
        .memberJobId(entity.getMemberJob().getId())
        .build();
  }

}
