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
public class ElectionVoterResponseDto {

  private Long memberId;
  private String realName;
  private String thumbnailPath;
  private Float generation;

  public static ElectionVoterResponseDto from(ElectionVoterEntity entity) {
    return ElectionVoterResponseDto.builder()
        .memberId(entity.getElectionVoterPK().getVoter().getId())
        .realName(entity.getElectionVoterPK().getVoter().getRealName())
        .thumbnailPath(entity.getElectionVoterPK().getVoter().getThumbnailPath())
        .generation(entity.getElectionVoterPK().getVoter().getGeneration())
        .build();
  }

}
