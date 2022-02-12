package keeper.project.homepage.dto.result;

import java.util.List;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostingResult extends CommonResult {

  private PostingEntity data;
  private List<FileEntity> files;
  private ThumbnailEntity thumbnail;
}
