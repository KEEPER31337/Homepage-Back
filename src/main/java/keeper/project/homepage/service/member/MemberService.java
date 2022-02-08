package keeper.project.homepage.service.member;

import java.util.Optional;
import keeper.project.homepage.dto.request.PointTransferRequest;
import keeper.project.homepage.dto.result.PointTransferResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.exception.CustomTransferPointLackException;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  public MemberEntity findById(Long id) throws RuntimeException {
    Optional<MemberEntity> memberEntity = memberRepository.findById(id);
    return memberEntity.orElse(null);
  }

  public PointTransferResult transferPoint(Long senderId,
      PointTransferRequest pointTransferRequest) {
    MemberEntity senderMember = memberRepository.findById(senderId)
        .orElseThrow(CustomMemberNotFoundException::new);
    MemberEntity receiverMember = memberRepository.findById(pointTransferRequest.getReceiverId())
        .orElseThrow(CustomMemberNotFoundException::new);

    if (senderMember.getPoint() < pointTransferRequest.getTransmissionPoint()) {
      throw new CustomTransferPointLackException("잔여 포인트가 부족합니다.");
    }

    int senderRemainingPoint = updateSenderPoint(senderMember,
        pointTransferRequest.getTransmissionPoint());
    int receiverRemainingPoint = updateReceiverPoint(receiverMember,
        pointTransferRequest.getTransmissionPoint());

    return new PointTransferResult(senderId, pointTransferRequest, senderRemainingPoint,
        receiverRemainingPoint);
  }

  public int updateSenderPoint(MemberEntity member, int point) {
    int remainingPoint = member.getPoint();
    member.updatePoint(remainingPoint - point);
    memberRepository.save(member);

    return member.getPoint();
  }

  public int updateReceiverPoint(MemberEntity member, int point) {
    int remainingPoint = member.getPoint();
    member.updatePoint(remainingPoint + point);
    memberRepository.save(member);

    return member.getPoint();
  }
}
