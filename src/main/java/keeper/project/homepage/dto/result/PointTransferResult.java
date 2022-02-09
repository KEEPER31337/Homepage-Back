package keeper.project.homepage.dto.result;

import keeper.project.homepage.dto.request.PointTransferRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointTransferResult {

  private Long senderId;
  private Long receiverId;
  private int transmissionPoint;
  private int senderRemainingPoint;
  private int receiverRemainingPoint;

  public PointTransferResult(Long senderId, PointTransferRequest pointTransferRequest,
      int senderRemainingPoint, int receiverRemainingPoint) {
    this.senderId = senderId;
    this.receiverId = pointTransferRequest.getReceiverId();
    this.transmissionPoint = pointTransferRequest.getTransmissionPoint();
    this.senderRemainingPoint = senderRemainingPoint;
    this.receiverRemainingPoint = receiverRemainingPoint;
  }

}
