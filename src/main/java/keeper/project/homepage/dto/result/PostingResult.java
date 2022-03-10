package keeper.project.homepage.dto.result;

import keeper.project.homepage.common.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostingResult extends CommonResult {

  private PostingEntity data;
}
