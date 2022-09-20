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
public class ElectionUpdateResponseDto {

  private Long electionId;
  private String name;
  private Boolean isAvailable;

  public static ElectionUpdateResponseDto from(ElectionEntity election) {
    return ElectionUpdateResponseDto.builder()
        .electionId(election.getId())
        .name(election.getName())
        .isAvailable(election.getIsAvailable())
        .build();
  }
}
