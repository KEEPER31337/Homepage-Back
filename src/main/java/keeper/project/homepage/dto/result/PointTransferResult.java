package keeper.project.homepage.dto.result;

import java.util.List;
import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.dto.request.PointTransferRequest;
import lombok.Getter;

@Getter
public class PointTransferResult {

  private final Long senderId;

  private final Long receiverId;

  private final Integer transmissionPoint;

  private List<MemberDto> memberDtoList;

  public PointTransferResult(PointTransferRequest pointTransferRequest, MemberDto sender,
      MemberDto receiver) {
    this.senderId = pointTransferRequest.getSenderId();
    this.receiverId = pointTransferRequest.getReceiverId();
    this.transmissionPoint = pointTransferRequest.getTransmissionPoint();
    memberDtoList.add(sender);
    memberDtoList.add(receiver);
  }

}
