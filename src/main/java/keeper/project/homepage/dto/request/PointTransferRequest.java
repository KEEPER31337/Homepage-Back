package keeper.project.homepage.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointTransferRequest {

  private Long receiverId;

  private int transmissionPoint;

}
