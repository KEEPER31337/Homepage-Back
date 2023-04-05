package keeper.project.homepage.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberGenerationRequestDto {

  private String memberLoginId;
  private Float generation;
}
