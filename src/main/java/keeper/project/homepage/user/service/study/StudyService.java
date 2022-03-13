package keeper.project.homepage.user.service.study;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.study.StudyEntity;
import keeper.project.homepage.entity.study.StudyHasMemberEntity;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.exception.study.CustomIpAddressNotFoundException;
import keeper.project.homepage.exception.study.CustomSeasonInvalidException;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.study.StudyHasMemberRepository;
import keeper.project.homepage.repository.study.StudyRepository;
import keeper.project.homepage.user.dto.study.StudyDto;
import keeper.project.homepage.user.dto.study.StudyYearSeasonDto;
import keeper.project.homepage.user.mapper.StudyMapper;
import keeper.project.homepage.util.ImageCenterCrop;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbnailSize;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StudyService {

  static private final Integer FIRST_SEMESTER = 1;
  static private final Integer SUMMER_SESSION = 2;
  static private final Integer SECOND_SEMESTER = 3;
  static private final Integer WINTER_SESSION = 4;

  private final StudyRepository studyRepository;
  private final StudyHasMemberRepository studyHasMemberRepository;
  private final ThumbnailService thumbnailService;
  private final MemberRepository memberRepository;
  private final StudyMapper studyMapper = Mappers.getMapper(StudyMapper.class);

  public List<StudyYearSeasonDto> getAllStudyYearsAndSeasons() {
    List<StudyYearSeasonDto> studyYearSeasonDtoList = new ArrayList<>();

    List<Integer> years = studyRepository.findDistinctYear();
    years.sort(Comparator.naturalOrder());
    for (Integer year : years) {
      StudyYearSeasonDto studyYearSeasonDto = new StudyYearSeasonDto();
      studyYearSeasonDto.setYear(year);
      studyYearSeasonDto.setSeason(studyRepository.findDistinctYearAndSeasonByYear(year));
      studyYearSeasonDtoList.add(studyYearSeasonDto);
    }
    return studyYearSeasonDtoList;
  }

  public List<StudyDto> getAllStudyList(Integer year, Integer season) {

    checkSeasonValidate(season);
    List<StudyEntity> studyEntities = studyRepository.findAllByYearAndSeason(year, season);

    List<StudyDto> studyDtos = new ArrayList<>();
    for (StudyEntity studyEntity : studyEntities) {
      studyDtos.add(studyMapper.toDto(studyEntity));
    }
    return studyDtos;
  }

  private void checkSeasonValidate(Integer season) {

    if (!FIRST_SEMESTER.equals(season) && !SUMMER_SESSION.equals(season) &&
        !SECOND_SEMESTER.equals(season) && !WINTER_SESSION.equals(season)) {
      throw new CustomSeasonInvalidException();
    }
  }

  private void checkIpAddressExist(String ipAddress) {

    if (ipAddress.isEmpty()) {
      throw new CustomIpAddressNotFoundException();
    }
  }

  @Transactional
  public StudyDto createStudy(
      StudyDto studyDto, MultipartFile thumbnail, List<Long> memberIdList) {

    checkSeasonValidate(studyDto.getSeason());
    checkIpAddressExist(studyDto.getIpAddress());

    studyDto.setRegisterTime(LocalDateTime.now());
    // TODO: 멤버 숫자에 스터디장도 포함해야 함
    studyDto.setMemberNumber(memberIdList.size());

    StudyEntity studyEntity = studyMapper.toEntity(studyDto);

    ThumbnailEntity studyThumbnail = saveThumbnail(studyDto.getIpAddress(), thumbnail);
    studyEntity.setThumbnail(studyThumbnail);
    studyRepository.save(studyEntity);

    for (Long memberId : memberIdList) {
      MemberEntity memberEntity = memberRepository.findById(memberId)
          .orElseThrow(() -> new CustomMemberNotFoundException("잘못 된 스터디원입니다."));

      StudyHasMemberEntity studyHasMemberEntity = new StudyHasMemberEntity(
          studyEntity, memberEntity);
      memberEntity.getStudyHasMemberEntities().add(studyHasMemberEntity);
      studyEntity.getStudyHasMemberEntities().add(studyHasMemberEntity);
      studyHasMemberRepository.save(studyHasMemberEntity);
    }

    return studyMapper.toDto(studyEntity);
  }

  private ThumbnailEntity saveThumbnail(String ipAddress, MultipartFile thumbnail) {
    ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
        thumbnail, ThumbnailSize.STUDY, ipAddress);

    if (thumbnailEntity == null) {
      throw new CustomThumbnailEntityNotFoundException();
    }
    return thumbnailEntity;
  }
}
