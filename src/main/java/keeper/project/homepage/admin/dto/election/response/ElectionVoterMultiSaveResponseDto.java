package keeper.project.homepage.admin.dto.election.response;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.entity.election.ElectionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElectionVoterMultiSaveResponseDto {

  private Long electionId;
  private Integer total;
  private List<Long> voterIds = new ArrayList<>();

  public ElectionVoterMultiSaveResponseDto(ElectionEntity election, Integer total) {
    this.electionId = election.getId();
    this.total = total;
  }

  public void increaseRegisterCount() {
    this.total += 1;
  }

  public void addVoterId(Long voterId) {
    this.voterIds.add(voterId);
  }

}
