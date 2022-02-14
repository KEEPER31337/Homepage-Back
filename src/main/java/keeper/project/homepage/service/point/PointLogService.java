package keeper.project.homepage.service.point;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import keeper.project.homepage.dto.request.PointGiftLogRequest;
import keeper.project.homepage.dto.request.PointLogRequest;
import keeper.project.homepage.dto.result.PointGiftLogResult;
import keeper.project.homepage.dto.result.PointLogResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import keeper.project.homepage.exception.CustomPointLogRequestNullException;
import keeper.project.homepage.exception.CustomTransferPointLackException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.point.PointLogRepository;
import keeper.project.homepage.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointLogService {

  private final PointLogRepository pointLogRepository;
  private final MemberRepository memberRepository;
  private final AuthService authService;

  public PointLogResult savePointLog(PointLogRequest pointLogRequest) {
    MemberEntity memberEntity = getMemberEntityWithJWT();

    int prePoint = memberEntity.getPoint();
    int finalPoint;

    if (pointLogRequest.getIsSpent() == 0) {
      finalPoint = increaseMemberPoint(memberEntity, pointLogRequest.getPoint()).getPoint();
    } else if (pointLogRequest.getIsSpent() == 1) {
      finalPoint = decreaseMemberPoint(memberEntity, pointLogRequest.getPoint()).getPoint();
    } else {
      throw new CustomPointLogRequestNullException();
    }

    PointLogEntity pointLogEntity = pointLogRepository.save(
        pointLogRequest.toEntity(memberEntity));

    return new PointLogResult(pointLogEntity, prePoint, finalPoint);
  }

  public PointGiftLogResult transferPoint(PointGiftLogRequest pointGiftLogRequest) {
    MemberEntity memberEntity = getMemberEntityWithJWT();

    if (memberEntity.getPoint() < pointGiftLogRequest.getPoint()) {
      throw new CustomTransferPointLackException("잔여 포인트가 부족합니다.");
    }

    MemberEntity presentedMember = memberRepository.findById(pointGiftLogRequest.getPresentedId())
        .orElseThrow(CustomMemberNotFoundException::new);

    int prePointMember = memberEntity.getPoint();
    int prePointPresented = presentedMember.getPoint();
    MemberEntity updateMember = decreaseMemberPoint(memberEntity, pointGiftLogRequest.getPoint());
    MemberEntity updatePresented = increaseMemberPoint(presentedMember,
        pointGiftLogRequest.getPoint());

    PointLogEntity pointLogEntity = pointGiftLogRequest.toEntity(updateMember, updatePresented);

    return new PointGiftLogResult(pointLogEntity, prePointMember, prePointPresented,
        updateMember.getPoint(), updatePresented.getPoint());
  }

  public List<PointLogResult> findAllPointLogByMember(Pageable pageable) {
    MemberEntity memberEntity = getMemberEntityWithJWT();

    return pointLogRepository.findAllByMemberAndPresentedMemberIsNull(memberEntity, pageable).stream()
        .map(PointLogResult::new).collect(Collectors.toList());
  }

  public List<PointGiftLogResult> findAllSentPointGiftLog(Pageable pageable) {
    MemberEntity memberEntity = getMemberEntityWithJWT();

    return pointLogRepository.findAllByMemberAndPresentedMemberIsNotNull(memberEntity, pageable).stream()
        .map(PointGiftLogResult::new).collect(Collectors.toList());
  }

  public List<PointGiftLogResult> findAllReceivedPointGiftLog(Pageable pageable) {
    MemberEntity memberEntity = getMemberEntityWithJWT();

    return pointLogRepository.findAllByPresentedMemberAndMemberIsNotNull(memberEntity, pageable).stream()
        .map(PointGiftLogResult::new).collect(Collectors.toList());
  }

  private MemberEntity increaseMemberPoint(MemberEntity member, int point) {
    int prePoint = member.getPoint();
    member.updatePoint(prePoint + point);

    return memberRepository.save(member);
  }

  private MemberEntity decreaseMemberPoint(MemberEntity member, int point) {
    int prePoint = member.getPoint();
    member.updatePoint(Math.max(prePoint - point, 0));

    return memberRepository.save(member);
  }

  private MemberEntity getMemberEntityWithJWT() {
    Long memberId = authService.getMemberIdByJWT();
    Optional<MemberEntity> member = memberRepository.findById(memberId);
    if (member.isEmpty()) {
      throw new CustomMemberNotFoundException();
    }
    return member.get();
  }

}
