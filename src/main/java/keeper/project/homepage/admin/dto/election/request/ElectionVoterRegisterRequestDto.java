package keeper.project.homepage.admin.dto.election.request;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.entity.election.ElectionVoterEntity;
import keeper.project.homepage.entity.election.ElectionVoterPK;
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
public class ElectionVoterRegisterRequestDto {

  @NotNull(message = "선거 ID는 필수 입력입니다.")
  private Long electionId;
  private List<Long> voterIds = new ArrayList<>();

  public ElectionVoterEntity toEntity(ElectionVoterPK pk) {
    return ElectionVoterEntity.builder()
        .electionVoterPK(pk)
        .isVoted(false)
        .build();
  }

}
