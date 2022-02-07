package keeper.project.homepage.dto.result;

import keeper.project.homepage.dto.member.MemberDto;
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

  public PointTransferResult(PointTransferRequest pointTransferRequest, MemberDto sender,
      MemberDto receiver) {
    this.senderId = pointTransferRequest.getSenderId();
    this.receiverId = pointTransferRequest.getReceiverId();
    this.transmissionPoint = pointTransferRequest.getTransmissionPoint();
    this.senderRemainingPoint = sender.getPoint();
    this.receiverRemainingPoint = receiver.getPoint();
  }

}
