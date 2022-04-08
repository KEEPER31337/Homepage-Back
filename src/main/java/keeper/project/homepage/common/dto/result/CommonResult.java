package keeper.project.homepage.common.dto.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult {

  private boolean success;

  private int code;

  private String msg;
}
