package keeper.project.homepage.election.dto.response;

import keeper.project.homepage.election.entity.ElectionEntity;
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
public class ElectionDeleteResponseDto {

  private Long electionId;
  private String name;

  public static ElectionDeleteResponseDto from(ElectionEntity election) {
    return ElectionDeleteResponseDto.builder()
        .electionId(election.getId())
        .name(election.getName())
        .build();
  }
}
