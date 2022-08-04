package keeper.project.homepage.admin.dto.election.response;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElectionCandidateMultiSaveResponseDto {

  private Integer total;
  private MemberJobEntity memberJob;
  private List<Long> candidateIds = new ArrayList<>();

  public ElectionCandidateMultiSaveResponseDto(Integer total, MemberJobEntity memberJob) {
    this.total = total;
    this.memberJob = memberJob;
  }

  public void increaseRegisterCount() {
    this.total += 1;
  }

  public void registerCandidateId(Long id) {
    this.candidateIds.add(id);
  }

}
