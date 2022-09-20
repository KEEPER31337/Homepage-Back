package keeper.project.homepage.election.dto.request;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.election.dto.ElectionCandidateDto;
import keeper.project.homepage.entity.election.ElectionCandidateEntity;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ElectionCandidateCreateRequestDto extends ElectionCandidateDto {

  @NotNull(message = "선거 ID는 필수 입력입니다.")
  private Long electionId;
  @NotNull(message = "선거 직위 ID는 필수 입력입니다.")
  private Long memberJobId;

  public ElectionCandidateEntity toEntity(MemberEntity candidate, ElectionEntity election,
      MemberJobEntity memberJob) {
    return ElectionCandidateEntity.builder()
        .candidate(candidate)
        .election(election)
        .description(getDescription())
        .registerTime(LocalDateTime.now())
        .voteCount(0)
        .memberJob(memberJob)
        .build();
  }
}
