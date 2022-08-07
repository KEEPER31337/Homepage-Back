package keeper.project.homepage.user.dto.election.response;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.election.ElectionEntity;
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
public class ElectionResponseDto {

  private Long electionId;
  private String name;
  private String description;
  private LocalDateTime registerTime;
  private Long creatorId;
  private Boolean isAvailable;

  public static ElectionResponseDto from(ElectionEntity entity) {
    return ElectionResponseDto.builder()
        .electionId(entity.getId())
        .name(entity.getName())
        .description(entity.getDescription())
        .registerTime(entity.getRegisterTime())
        .creatorId(entity.getCreator().getId())
        .isAvailable(entity.getIsAvailable())
        .build();
  }

}
