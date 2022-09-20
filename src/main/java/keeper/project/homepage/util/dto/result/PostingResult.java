package keeper.project.homepage.util.dto.result;

import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostingResult extends CommonResult {

  private PostingEntity data;
}
