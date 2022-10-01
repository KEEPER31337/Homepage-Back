package keeper.project.homepage.clerk.dto.request;

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
public class ClerkMemberTypeRequestDto {

  @NotNull
  private Long memberId;

  @NotNull
  private Long typeId;
}
