package keeper.project.homepage.user.service.point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import keeper.project.homepage.point.exception.CustomPointLogRequestNullException;
import keeper.project.homepage.user.dto.point.request.PointGiftLogRequestDto;
import keeper.project.homepage.user.dto.point.request.PointLogRequestDto;
import keeper.project.homepage.user.dto.point.response.PointGiftLogResponseDto;
import keeper.project.homepage.user.dto.point.response.PointLogResponseDto;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.point.entity.PointLogEntity;
import keeper.project.homepage.point.exception.CustomPointLackException;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.point.PointLogRepository;
import keeper.project.homepage.util.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

  private void checkPointLack(int previousPoint, int requestPoint) {
    if (previousPoint < requestPoint) {
      throw new CustomPointLackException();
    }
  }

  private void checkPointLogRequest(PointLogRequestDto pointLogRequestDto) {
    if (pointLogRequestDto.getTime() == null || pointLogRequestDto.getPoint() == null) {
      throw new CustomPointLogRequestNullException();
    }
  }

  private void checkPointGiftLogRequest(PointGiftLogRequestDto pointGiftLogRequestDto) {
    if (pointGiftLogRequestDto.getTime() == null || pointGiftLogRequestDto.getPoint() == null) {
      throw new CustomPointLogRequestNullException();
    }
  }

  public PointLogResponseDto createPointUseLog(MemberEntity member,
      PointLogRequestDto pointLogRequestDto) {
    int previousPoint = member.getPoint();

    checkPointLogRequest(pointLogRequestDto);
    checkPointLack(previousPoint, pointLogRequestDto.getPoint());

    int finalPoint = updateMemberPoint(member,
        previousPoint - pointLogRequestDto.getPoint()).getPoint();

    PointLogEntity pointLogEntity = pointLogRepository.save(pointLogRequestDto.toEntity(member, 1));

    return new PointLogResponseDto(pointLogEntity, previousPoint, finalPoint);
  }

  public PointLogResponseDto createPointSaveLog(MemberEntity member,
      PointLogRequestDto pointLogRequestDto) {
    checkPointLogRequest(pointLogRequestDto);

    int previousPoint = member.getPoint();
    int finalPoint = updateMemberPoint(member,
        previousPoint + pointLogRequestDto.getPoint()).getPoint();

    PointLogEntity pointLogEntity = pointLogRepository.save(pointLogRequestDto.toEntity(member, 0));

    return new PointLogResponseDto(pointLogEntity, previousPoint, finalPoint);
  }

  public PointGiftLogResponseDto presentingPoint(PointGiftLogRequestDto pointGiftLogRequestDto) {
    checkPointGiftLogRequest(pointGiftLogRequestDto);
    MemberEntity presentedMember = memberRepository.findById(
            pointGiftLogRequestDto.getPresentedId())
        .orElseThrow(CustomMemberNotFoundException::new);

    MemberEntity memberEntity = authService.getMemberEntityWithJWT();

    checkPointLack(memberEntity.getPoint(), pointGiftLogRequestDto.getPoint());

    int prePointMember = memberEntity.getPoint();
    int prePointPresented = presentedMember.getPoint();

    MemberEntity updateMember = updateMemberPoint(memberEntity,
        prePointMember - pointGiftLogRequestDto.getPoint());
    MemberEntity updatePresented = updateMemberPoint(presentedMember,
        prePointPresented + pointGiftLogRequestDto.getPoint());

    PointLogEntity pointLogEntity = pointLogRepository.save(
        pointGiftLogRequestDto.toEntity(updateMember, updatePresented, 1));
    pointLogRepository.save(pointGiftLogRequestDto.toEntity(updatePresented, updateMember, 0));

    return new PointGiftLogResponseDto(pointLogEntity, prePointMember, prePointPresented,
        updateMember.getPoint(), updatePresented.getPoint());
  }

  public Map<String, Object> getPointLogs(Pageable pageable) {
    MemberEntity memberEntity = authService.getMemberEntityWithJWT();

    List<PointLogResponseDto> pointLogResponseDtoList = new ArrayList<>();
    Map<String, Object> result = new HashMap<>();
    Page<PointLogEntity> pointLogEntityPage = pointLogRepository.findAllByMember(
        memberEntity, pageable);

    for (PointLogEntity pointLogEntity : pointLogEntityPage.getContent()) {
      PointLogResponseDto pointLogResponseDto = new PointLogResponseDto(pointLogEntity);
      pointLogResponseDtoList.add(pointLogResponseDto);
    }

    result.put("isLast", pointLogEntityPage.isLast());
    result.put("content", pointLogResponseDtoList);

    return result;
  }

}
