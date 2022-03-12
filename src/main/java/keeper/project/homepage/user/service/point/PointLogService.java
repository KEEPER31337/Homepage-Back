package keeper.project.homepage.user.service.point;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.user.dto.point.request.PointGiftLogRequestDto;
import keeper.project.homepage.user.dto.point.request.PointLogRequestDto;
import keeper.project.homepage.user.dto.point.result.PointGiftLogResultDto;
import keeper.project.homepage.user.dto.point.result.PointLogResultDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import keeper.project.homepage.exception.point.CustomPointLackException;
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

  private MemberEntity updateMemberPoint(MemberEntity member, int newPoint) {
    member.updatePoint(newPoint);

    return memberRepository.save(member);
  }

  public PointLogResultDto createPointUseLog(MemberEntity member,
      PointLogRequestDto pointLogRequestDto) {
    int previousPoint = member.getPoint();

    if (previousPoint < pointLogRequestDto.getPoint()) {
      throw new CustomPointLackException("잔여 포인트가 부족합니다.");
    }

    int finalPoint = updateMemberPoint(member,
        previousPoint - pointLogRequestDto.getPoint()).getPoint();

    PointLogEntity pointLogEntity = pointLogRepository.save(pointLogRequestDto.toEntity(member, 1));

    return new PointLogResultDto(pointLogEntity, previousPoint, finalPoint);
  }

  public PointLogResultDto createPointSaveLog(MemberEntity member,
      PointLogRequestDto pointLogRequestDto) {
    int previousPoint = member.getPoint();
    int finalPoint = updateMemberPoint(member,
        previousPoint + pointLogRequestDto.getPoint()).getPoint();

    PointLogEntity pointLogEntity = pointLogRepository.save(pointLogRequestDto.toEntity(member, 0));

    return new PointLogResultDto(pointLogEntity, previousPoint, finalPoint);
  }

  public PointGiftLogResultDto presentingPoint(PointGiftLogRequestDto pointGiftLogRequestDto) {
    MemberEntity presentedMember = memberRepository.findById(
            pointGiftLogRequestDto.getPresentedId())
        .orElseThrow(CustomMemberNotFoundException::new);

    MemberEntity memberEntity = authService.getMemberEntityWithJWT();

    if (memberEntity.getPoint() < pointGiftLogRequestDto.getPoint()) {
      throw new CustomPointLackException("잔여 포인트가 부족합니다.");
    }

    int prePointMember = memberEntity.getPoint();
    int prePointPresented = presentedMember.getPoint();

    MemberEntity updateMember = updateMemberPoint(memberEntity,
        prePointMember - pointGiftLogRequestDto.getPoint());
    MemberEntity updatePresented = updateMemberPoint(presentedMember,
        prePointPresented + pointGiftLogRequestDto.getPoint());

    PointLogEntity pointLogEntity = pointGiftLogRequestDto.toEntity(updateMember, updatePresented);

    return new PointGiftLogResultDto(pointLogEntity, prePointMember, prePointPresented,
        updateMember.getPoint(), updatePresented.getPoint());
  }

  public List<PointLogResultDto> getPointLogs(Pageable pageable) {
    MemberEntity memberEntity = authService.getMemberEntityWithJWT();

    List<PointLogResultDto> pointLogResultDtoList = new ArrayList<>();
    List<PointLogEntity> pointLogEntityList = pointLogRepository.findAllByMemberOrPresentedMember(
        memberEntity, memberEntity, pageable);

    for(PointLogEntity pointLogEntity : pointLogEntityList) {
      PointLogResultDto pointLogResultDto = new PointLogResultDto(pointLogEntity);
      pointLogResultDtoList.add(pointLogResultDto);
    }

    return pointLogResultDtoList;
  }

}
