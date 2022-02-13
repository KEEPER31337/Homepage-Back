package keeper.project.homepage.dto.posting;

import keeper.project.homepage.dto.result.CommonResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeAndDislikeDto {

  private boolean isLiked;
  private boolean isDisliked;
}
