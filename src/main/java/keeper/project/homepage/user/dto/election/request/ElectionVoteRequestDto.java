package keeper.project.homepage.user.dto.election.request;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
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
public class ElectionVoteRequestDto {

  @NotNull(message = "선거자 멤버 ID는 필수 입력입니다.")
  private Long voterId;
  @NotNull(message = "선거 ID는 필수 입력입니다.")
  private Long electionId;
  private List<Long> candidateIds = new ArrayList<>();

}
