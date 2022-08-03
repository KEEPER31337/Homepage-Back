package keeper.project.homepage.admin.dto.election.response;

import keeper.project.homepage.entity.election.ElectionEntity;
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
public class ElectionResponseDto {

  private Long electionId;
  private String name;
  private Boolean isAvailable;

  public static ElectionResponseDto from(ElectionEntity election) {
    return ElectionResponseDto.builder()
        .electionId(election.getId())
        .name(election.getName())
        .isAvailable(election.getIsAvailable())
        .build();
  }
}
