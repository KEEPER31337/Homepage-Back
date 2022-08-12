package keeper.project.homepage.user.dto.election.response;

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
public class ElectionVoteStatus {

  private Integer total;
  private Integer voted;
  private String rate;
  private Boolean isOpen;

  public static ElectionVoteStatus createStatus(Integer total, Integer voted, Boolean isOpen) {
    Double voteRate = total == 0 ? 0 : (Double.valueOf(voted)/total) * 100;
    return ElectionVoteStatus.builder()
        .total(total)
        .voted(voted)
        .rate(String.format("%.02f", voteRate))
        .isOpen(isOpen)
        .build();
  }

}
