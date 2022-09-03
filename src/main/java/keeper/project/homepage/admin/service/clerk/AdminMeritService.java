package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import keeper.project.homepage.admin.dto.clerk.request.MeritAddRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.MeritLogUpdateRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.MeritTypeCreateRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.MemberTotalMeritLogsResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.MeritLogByYearResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.MeritAddResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.MeritTypeResponseDto;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.clerk.MeritLogEntity;
import keeper.project.homepage.entity.clerk.MeritTypeEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomDuplicateAbsenceLogException;
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
  public List<MeritAddResponseDto> addMeritsWithLogs(List<MeritAddRequestDto> requestDtoList) {
    List<MeritAddResponseDto> responseDtoList = new ArrayList<>();
    for (MeritAddRequestDto requestDto : requestDtoList) {
      responseDtoList.add(MeritAddResponseDto.builder()
          .meritLogId(addMeritWithLog(requestDto).getId())
          .build());
    }
    return responseDtoList;
  }

  @Transactional
  public MeritLogEntity addMeritWithLog(MeritAddRequestDto requestDto) {
    MemberEntity giver = authService.getMemberEntityWithJWT();
    MemberEntity awarder = memberUtilService.getById(requestDto.getMemberId());
    MeritTypeEntity meritType = meritTypeRepository.findById(requestDto.getMeritTypeId())
        .orElseThrow(CustomMeritTypeNotFoundException::new);
    MeritLogEntity meritLog = getMeritLog(requestDto.getDate(), meritType, awarder, giver);
    addMeritByMeritType(awarder, meritType);
    return meritLogRepository.save(meritLog);
  }


  protected static void addMeritByMeritType(MemberEntity awarder, MeritTypeEntity meritType) {
    if (meritType.getIsMerit()) {
      awarder.changeMerit(awarder.getMerit() + meritType.getMerit());
    } else {
      awarder.changeDemerit(awarder.getDemerit() + meritType.getMerit());
    }
  }

  private static MeritLogEntity getMeritLog(LocalDate date,
      MeritTypeEntity meritType, MemberEntity awarder, MemberEntity giver) {
    return MeritLogEntity.builder()
        .awarder(awarder)
        .giver(giver)
        .meritType(meritType)
        .date(date)
        .date(date)
        .build();
  }

  public List<Integer> getYears() {
    MeritLogEntity meritLog = meritLogRepository.findFirstByOrderByDate()
        .orElseThrow(CustomMeritLogNotFoundException::new);
    return IntStream.range(meritLog.getDate().getYear(), LocalDate.now().getYear() + 1)
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
  public Long deleteMeritWithLog(Long meritLogId) {
    MeritLogEntity meritLog = meritLogRepository.findById(meritLogId)
        .orElseThrow(CustomMeritLogNotFoundException::new);

    deleteMeritByMeritType(meritLog.getAwarder(), meritLog.getMeritType());
    meritLogRepository.delete(meritLog);

    return meritLog.getId();
  }

  @Transactional
  public Long updateMeritLog(MeritLogUpdateRequestDto requestDto) {
    MeritLogEntity meritLog = meritLogRepository.findById(requestDto.getMeritLogId())
        .orElseThrow(CustomMeritLogNotFoundException::new);
    MeritTypeEntity findMeritType = meritTypeRepository.findById(requestDto.getMeritTypeId())
        .orElseThrow(CustomMeritTypeNotFoundException::new);

    deleteMeritByMeritType(meritLog.getAwarder(), meritLog.getMeritType());
    addMeritByMeritType(meritLog.getAwarder(), findMeritType);

    meritLog.changeMeritType(findMeritType);
    meritLog.changeDate(requestDto.getDate());

    return meritLog.getId();
  }

  void deleteAbsenceLog(MemberEntity awarder, LocalDate date) {
    MeritTypeEntity absence = meritTypeRepository.findByDetail(ABSENCE.getType())
        .orElseThrow(CustomMeritTypeNotFoundException::new);
    List<MeritLogEntity> absenceLog = meritLogRepository.findByAwarderAndMeritTypeAndDate(
        awarder, absence, date);
    if (absenceLog.isEmpty()) {
      throw new CustomMeritLogNotFoundException();
    }
    if (absenceLog.size() > 1) {
      throw new CustomDuplicateAbsenceLogException();
    }
    meritLogRepository.delete(absenceLog.get(0));
    deleteMeritByMeritType(awarder, absence);
  }

  void deleteMeritByMeritType(MemberEntity awarder, MeritTypeEntity meritType) {
    if (meritType.getIsMerit()) {
      awarder.changeMerit(awarder.getMerit() - meritType.getMerit());
    } else {
      awarder.changeDemerit(awarder.getDemerit() - meritType.getMerit());
    }
  }
}
