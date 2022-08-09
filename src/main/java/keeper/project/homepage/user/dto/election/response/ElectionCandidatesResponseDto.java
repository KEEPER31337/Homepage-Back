package keeper.project.homepage.user.dto.election.response;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
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
public class ElectionCandidatesResponseDto {

  private Long candidateId;
  private Long memberId;
  private Long electionId;
  private Long memberJobId;
  private String description;
  private LocalDateTime registerTime;

  public static ElectionCandidatesResponseDto from(ElectionCandidateEntity entity) {
    return ElectionCandidatesResponseDto.builder()
        .candidateId(entity.getId())
        .memberId(entity.getCandidate().getId())
        .electionId(entity.getElection().getId())
        .memberJobId(entity.getMemberJob().getId())
        .description(entity.getDescription())
        .registerTime(entity.getRegisterTime())
        .build();
  }

}
