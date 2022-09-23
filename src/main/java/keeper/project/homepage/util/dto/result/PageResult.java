package keeper.project.homepage.util.dto.result;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class PageResult<T> extends CommonResult {

  private Page<T> page;
}
