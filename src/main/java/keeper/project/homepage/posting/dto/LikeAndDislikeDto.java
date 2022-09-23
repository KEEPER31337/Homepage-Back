package keeper.project.homepage.posting.dto;

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
