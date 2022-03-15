package keeper.project.homepage.user.dto.posting;

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
