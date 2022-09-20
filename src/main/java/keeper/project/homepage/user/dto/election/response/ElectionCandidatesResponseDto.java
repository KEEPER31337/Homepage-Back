package keeper.project.homepage.user.dto.election.response;

import java.time.LocalDateTime;
import keeper.project.homepage.election.entity.ElectionCandidateEntity;
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
public class ElectionCandidatesResponseDto {

  private Long candidateId;
  private Long memberId;
  private String realName;
  private String thumbnailPath;
  private Float generation;
  private String description;
  private LocalDateTime registerTime;

  public static ElectionCandidatesResponseDto from(ElectionCandidateEntity entity) {
    return ElectionCandidatesResponseDto.builder()
        .candidateId(entity.getId())
        .memberId(entity.getCandidate().getId())
        .realName(entity.getCandidate().getRealName())
        .thumbnailPath(entity.getCandidate().getThumbnailPath())
        .generation(entity.getCandidate().getGeneration())
        .description(entity.getDescription())
        .registerTime(entity.getRegisterTime())
        .build();
  }

}
