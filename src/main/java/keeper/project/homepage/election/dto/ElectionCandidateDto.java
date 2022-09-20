package keeper.project.homepage.election.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.election.entity.ElectionCandidateEntity;
import keeper.project.homepage.election.entity.ElectionEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
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
public class ElectionCandidateDto {

  @NotNull(message = "후보자 ID 입력은 필수입니다.")
  private Long memberId;
  private String description;

  public ElectionCandidateEntity toEntity(MemberEntity candidate, ElectionEntity election,
      MemberJobEntity job) {
    return ElectionCandidateEntity.builder()
        .candidate(candidate)
        .election(election)
        .description(this.description)
        .registerTime(LocalDateTime.now())
        .voteCount(0)
        .memberJob(job)
        .build();
  }
}
