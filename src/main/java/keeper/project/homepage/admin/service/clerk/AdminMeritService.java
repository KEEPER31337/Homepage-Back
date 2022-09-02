package keeper.project.homepage.admin.service.clerk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import keeper.project.homepage.admin.dto.clerk.request.MeritAddRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.MeritTypeCreateRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.MemberTotalMeritLogsResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.MeritLogByYearResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.MeritAddResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.MeritTypeResponseDto;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.clerk.MeritLogEntity;
import keeper.project.homepage.entity.clerk.MeritTypeEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomMeritLogNotFoundException;
import keeper.project.homepage.exception.clerk.CustomMeritTypeNotFoundException;
import keeper.project.homepage.repository.clerk.MeritLogRepository;
import keeper.project.homepage.repository.clerk.MeritTypeRepository;
import keeper.project.homepage.user.service.member.MemberUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMeritService {

  private final MeritLogRepository meritLogRepository;

  private final MeritTypeRepository meritTypeRepository;

  private final MemberUtilService memberUtilService;

  private final AuthService authService;

  public List<MeritLogByYearResponseDto> getMeritLogByYear(Integer year) {
    List<MeritLogEntity> meritLogs = meritLogRepository.findAllByYear(year);

    return meritLogs.stream()
        .map(MeritLogByYearResponseDto::from)
        .toList();
  }

  public List<MemberTotalMeritLogsResponseDto> getMemberTotalMeritLogs() {
    List<MeritLogEntity> meritLogs = meritLogRepository.findAll();
    Map<MemberEntity, List<MeritLogEntity>> meritLogMap = meritLogs.stream()
        .collect(Collectors.groupingBy(MeritLogEntity::getAwarder));
    return meritLogMap.entrySet()
        .stream()
        .map(entry -> MemberTotalMeritLogsResponseDto.of(entry.getKey(), entry.getValue()))
        .toList();
  }

  @Transactional
  public List<MeritAddResponseDto> addMeritLogs(List<MeritAddRequestDto> requestDtoList) {
    MemberEntity giver = authService.getMemberEntityWithJWT();
    List<MeritAddResponseDto> responseDtoList = new ArrayList<>();

    for (MeritAddRequestDto requestDto : requestDtoList) {
      MemberEntity awarder = memberUtilService.getById(requestDto.getMemberId());
      MeritTypeEntity meritType = meritTypeRepository.findById(requestDto.getMeritTypeId())
          .orElseThrow(CustomMeritTypeNotFoundException::new);
      MeritLogEntity meritLog = getMeritLogEntity(requestDto.getDate(), meritType, awarder, giver);

      updateMeritByMeritType(awarder, meritType);
      MeritLogEntity save = meritLogRepository.save(meritLog);
      responseDtoList.add(MeritAddResponseDto.builder()
          .meritLogId(save.getId())
          .build());
    }
    return responseDtoList;
  }

  protected static void updateMeritByMeritType(MemberEntity awarder, MeritTypeEntity meritType) {
    if (meritType.getIsMerit()) {
      awarder.changeMerit(awarder.getMerit() + meritType.getMerit());
    } else {
      awarder.changeDemerit(awarder.getDemerit() + meritType.getMerit());
    }
  }

  private static MeritLogEntity getMeritLogEntity(LocalDate date,
      MeritTypeEntity meritType, MemberEntity awarder, MemberEntity giver) {
    return MeritLogEntity.builder()
        .awarder(awarder)
        .giver(giver)
        .meritType(meritType)
        .time(date)
        .build();
  }

  public List<Integer> getYears() {
    MeritLogEntity meritLog = meritLogRepository.findFirstByOrderByTime()
        .orElseThrow(CustomMeritLogNotFoundException::new);
    return IntStream.range(meritLog.getTime().getYear(), LocalDate.now().getYear() + 1)
        .boxed()
        .toList();
  }

  @Transactional
  public Long createMeritType(MeritTypeCreateRequestDto requestDto) {
    return meritTypeRepository.save(
        MeritTypeEntity.newInstance(requestDto.getMerit(), requestDto.getIsMerit(),
            requestDto.getDetail())).getId();
  }

  public Long deleteMeritType(Long typeId) {
    MeritTypeEntity meritTypeEntity = meritTypeRepository.findById(typeId)
        .orElseThrow(CustomMeritTypeNotFoundException::new);
    meritTypeRepository.delete(meritTypeEntity);
    return meritTypeEntity.getId();
  }

  public List<MeritTypeResponseDto> getMeritTypes() {
    return meritTypeRepository.findAll()
        .stream()
        .map(MeritTypeResponseDto::from)
        .toList();
  }

  @Transactional
  public Long deleteMerit(Long meritLogId) {
    MeritLogEntity meritLog = meritLogRepository.findById(meritLogId)
        .orElseThrow(CustomMeritLogNotFoundException::new);

    deleteMeritByMeritType(meritLog.getAwarder(), meritLog.getMeritType());
    meritLogRepository.delete(meritLog);

    return meritLog.getId();
  }

  void deleteMeritByMeritType(MemberEntity awarder, MeritTypeEntity meritType) {
    if (meritType.getIsMerit()) {
      awarder.changeMerit(awarder.getMerit() - meritType.getMerit());
    } else {
      awarder.changeDemerit(awarder.getDemerit() - meritType.getMerit());
    }
  }
}
