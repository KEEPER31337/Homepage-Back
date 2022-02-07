package keeper.project.homepage.dto.request;

import lombok.Getter;

@Getter
public class PointTransferRequest {

  private Long senderId;

  private Long receiverId;

  private Integer transmissionPoint;

}
