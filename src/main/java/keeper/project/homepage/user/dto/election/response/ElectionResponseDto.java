package keeper.project.homepage.user.dto.election.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElectionResponseDto {

  private Long electionId;
  private String name;
  private String description;


}
