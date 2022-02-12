package keeper.project.homepage.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeAndDislikeResult extends CommonResult {

  private boolean isLiked;
  private boolean isDisliked;
}
