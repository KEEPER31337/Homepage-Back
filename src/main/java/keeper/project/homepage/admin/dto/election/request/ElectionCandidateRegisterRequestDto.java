package keeper.project.homepage.admin.dto.election.request;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.admin.dto.election.ElectionCandidateDto;
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
public class ElectionCandidateRegisterRequestDto {

  @NotNull(message = "선거 ID는 필수 입력입니다.")
  private Long electionId;
  @NotNull(message = "선거 직위 ID는 필수 입력입니다.")
  private Long memberJobId;
  @Valid
  private List<ElectionCandidateDto> candidates = new ArrayList<>();

}
