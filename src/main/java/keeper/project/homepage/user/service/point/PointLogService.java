package keeper.project.homepage.user.service.point;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.dto.point.request.PointGiftLogRequest;
import keeper.project.homepage.dto.point.request.PointLogRequest;
import keeper.project.homepage.dto.point.result.PointGiftLogResult;
import keeper.project.homepage.dto.point.result.PointLogResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import keeper.project.homepage.exception.CustomTransferPointLackException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.point.PointLogRepository;
import keeper.project.homepage.common.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointLogService {

  private final PointLogRepository pointLogRepository;
  private final MemberRepository memberRepository;
  private final AuthService authService;

  public PointLogResult createPointUseLog(MemberEntity member,
      PointLogRequest pointLogRequest) {
    int previousPoint = member.getPoint();

    if (previousPoint < pointLogRequest.getPoint()) {
      throw new CustomTransferPointLackException("잔여 포인트가 부족합니다.");
    }

    int finalPoint = updateMemberPoint(member,
        previousPoint - pointLogRequest.getPoint()).getPoint();

    PointLogEntity pointLogEntity = pointLogRepository.save(pointLogRequest.toEntity(member, 1));

    return new PointLogResult(pointLogEntity, previousPoint, finalPoint);
  }

  public PointLogResult createPointSaveLog(MemberEntity member,
      PointLogRequest pointLogRequest) {
    int previousPoint = member.getPoint();
    int finalPoint = updateMemberPoint(member,
        previousPoint + pointLogRequest.getPoint()).getPoint();

    PointLogEntity pointLogEntity = pointLogRepository.save(pointLogRequest.toEntity(member, 0));

    return new PointLogResult(pointLogEntity, previousPoint, finalPoint);
  }

  public PointGiftLogResult transferPoint(PointGiftLogRequest pointGiftLogRequest) {
    MemberEntity memberEntity = authService.getMemberEntityWithJWT();

    if (memberEntity.getPoint() < pointGiftLogRequest.getPoint()) {
      throw new CustomTransferPointLackException("잔여 포인트가 부족합니다.");
    }

    MemberEntity presentedMember = memberRepository.findById(pointGiftLogRequest.getPresentedId())
        .orElseThrow(CustomMemberNotFoundException::new);

    int prePointMember = memberEntity.getPoint();
    int prePointPresented = presentedMember.getPoint();

    MemberEntity updateMember = updateMemberPoint(memberEntity,
        prePointMember - pointGiftLogRequest.getPoint());
    MemberEntity updatePresented = updateMemberPoint(presentedMember,
        prePointPresented + pointGiftLogRequest.getPoint());

    PointLogEntity pointLogEntity = pointGiftLogRequest.toEntity(updateMember, updatePresented);

    return new PointGiftLogResult(pointLogEntity, prePointMember, prePointPresented,
        updateMember.getPoint(), updatePresented.getPoint());
  }

  public List<PointLogResult> findAllPointLogByMember(Pageable pageable) {
    MemberEntity memberEntity = authService.getMemberEntityWithJWT();

    return pointLogRepository.findAllByMemberAndPresentedMemberIsNull(memberEntity, pageable)
        .stream()
        .map(PointLogResult::new).sorted(Comparator.comparing(PointLogResult::getTime).reversed())
        .collect(Collectors.toList());
  }

  public List<PointGiftLogResult> findAllSentPointGiftLog(Pageable pageable) {
    MemberEntity memberEntity = authService.getMemberEntityWithJWT();

    return pointLogRepository.findAllByMemberAndPresentedMemberIsNotNull(memberEntity, pageable)
        .stream()
        .map(PointGiftLogResult::new)
        .sorted(Comparator.comparing(PointGiftLogResult::getTime).reversed())
        .collect(Collectors.toList());
  }

  public List<PointGiftLogResult> findAllReceivedPointGiftLog(Pageable pageable) {
    MemberEntity memberEntity = authService.getMemberEntityWithJWT();

    return pointLogRepository.findAllByPresentedMemberAndMemberIsNotNull(memberEntity, pageable)
        .stream()
        .map(PointGiftLogResult::new)
        .sorted(Comparator.comparing(PointGiftLogResult::getTime).reversed())
        .collect(Collectors.toList());
  }

  private MemberEntity updateMemberPoint(MemberEntity member, int newPoint) {
    member.updatePoint(newPoint);

    return memberRepository.save(member);
  }

}
