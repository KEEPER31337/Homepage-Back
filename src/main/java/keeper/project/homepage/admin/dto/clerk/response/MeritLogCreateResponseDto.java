package keeper.project.homepage.admin.dto.clerk.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeritLogCreateResponseDto {

  @NonNull
  private Long meritLogId;
}
