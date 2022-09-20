package keeper.project.homepage.user.dto.election.response;

import keeper.project.homepage.election.entity.ElectionChartLogEntity;
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
public class ElectionResultResponseDto {

  private Long memberId;
  private String name;

  public static ElectionResultResponseDto from(ElectionChartLogEntity entity) {
    return ElectionResultResponseDto.builder()
        .memberId(entity.getElectionCandidate().getCandidate().getId())
        .name(entity.getElectionCandidate().getCandidate().getRealName())
        .build();
  }

}
