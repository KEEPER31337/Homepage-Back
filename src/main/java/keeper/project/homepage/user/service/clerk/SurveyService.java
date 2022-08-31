package keeper.project.homepage.user.service.clerk;

import static keeper.project.homepage.entity.clerk.SurveyReplyEntity.SurveyReply.OTHER_DORMANT;

import io.micrometer.core.instrument.util.StringEscapeUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.admin.dto.clerk.response.ClosedSurveyInformationResponseDto;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyExcuseEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomSurveyInVisibleException;
import keeper.project.homepage.exception.clerk.CustomSurveyMemberReplyNotFoundException;
import keeper.project.homepage.exception.clerk.CustomSurveyNotFoundException;
import keeper.project.homepage.repository.clerk.SurveyMemberReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyExcuseRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyRepository;
import keeper.project.homepage.user.dto.clerk.response.SurveyModifyResponseDto;
import keeper.project.homepage.user.dto.clerk.request.SurveyResponseRequestDto;
import keeper.project.homepage.user.dto.clerk.response.SurveyInformationResponseDto;
import keeper.project.homepage.user.service.member.MemberUtilService;
import keeper.project.homepage.util.service.SurveyUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

  private final SurveyRepository surveyRepository;
  private final SurveyMemberReplyRepository surveyMemberReplyRepository;
  private final SurveyReplyRepository surveyReplyRepository;
  private final SurveyReplyExcuseRepository surveyReplyExcuseRepository;
  private final SurveyUtilService surveyUtilService;
  private final MemberUtilService memberUtilService;
  private final AuthService authService;

  static final SurveyEntity NO_SURVEY = SurveyEntity.builder().id(-1L).build();

  @Transactional
  public Long responseSurvey(Long surveyId, SurveyResponseRequestDto requestDto) {
    SurveyEntity survey = surveyUtilService.getSurveyById(surveyId);
    validateVisibleSurvey(survey);

    MemberEntity member = memberUtilService.getById(requestDto.getMemberId());
    SurveyReplyEntity reply = surveyUtilService.getReplyById(
        requestDto.getReplyId());

    SurveyMemberReplyEntity memberReply = surveyUtilService.generateSurveyMemberReplyEntity(survey,
        member, reply);

    saveSurveyMemberReply(survey, memberReply);
    saveSurveyReplyExcuse(reply, memberReply, requestDto);

    return surveyMemberReplyRepository.getById(memberReply.getId()).getId();
  }

  private void saveSurveyMemberReply(SurveyEntity survey,
      SurveyMemberReplyEntity memberReply) {
    surveyMemberReplyRepository.save(memberReply);
    survey.getRespondents().add(memberReply);
  }

  private void saveSurveyReplyExcuse(SurveyReplyEntity reply,
      SurveyMemberReplyEntity memberReply,
      SurveyResponseRequestDto requestDto) {
    if (reply.getId() == OTHER_DORMANT.getId()) {
      SurveyReplyExcuseEntity excuse = SurveyReplyExcuseEntity.builder()
          .surveyMemberReplyEntity(memberReply)
          .restExcuse(requestDto.getExcuse())
          .build();
      surveyReplyExcuseRepository.save(excuse);
    }
  }

  private void validateVisibleSurvey(SurveyEntity survey) {
    if (survey.getIsVisible().equals(false)) {
      throw new CustomSurveyInVisibleException();
    }
  }

  public SurveyModifyResponseDto modifyResponse(Long surveyId,
      SurveyResponseRequestDto requestDto) {
    MemberEntity member = memberUtilService.getById(requestDto.getMemberId());
    SurveyMemberReplyEntity memberReply = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
            surveyId, member.getId())
        .orElseThrow(CustomSurveyMemberReplyNotFoundException::new);
    SurveyReplyEntity reply = surveyReplyRepository.getById(requestDto.getReplyId());

    modifyReplyAndExcuse(memberReply, reply, requestDto);

    return SurveyModifyResponseDto.from(surveyMemberReplyRepository.save(memberReply),
        requestDto.getExcuse());
  }

  private void modifyReplyAndExcuse(SurveyMemberReplyEntity memberReply, SurveyReplyEntity reply,
      SurveyResponseRequestDto requestDto) {
    memberReply.modifyReply(reply);

    SurveyReplyExcuseEntity beforeExcuse = memberReply.getSurveyReplyExcuseEntity();

    if (isBeforeExcuseNull(beforeExcuse)) {
      if (isReplyOtherDormant(requestDto)) {
        surveyUtilService.generateSurveyReplyExcuse(memberReply,
            requestDto.getExcuse());
      }
    }
    if (!isBeforeExcuseNull(beforeExcuse)) {
      if (isReplyOtherDormant(requestDto)) {
        memberReply.getSurveyReplyExcuseEntity()
            .modifyExcuse(requestDto.getExcuse());
      }
      deleteBeforeExcuse(beforeExcuse);
      setExcuse(memberReply, requestDto);
    }
  }

  private Boolean isBeforeExcuseNull(SurveyReplyExcuseEntity excuse) {
    return excuse == null;
  }

  private Boolean isReplyOtherDormant(SurveyResponseRequestDto requestDto) {
    return requestDto.getReplyId().equals(OTHER_DORMANT.getId());
  }

  private void deleteBeforeExcuse(SurveyReplyExcuseEntity beforeExcuse) {
    surveyReplyExcuseRepository.delete(beforeExcuse);
  }

  private void setExcuse(SurveyMemberReplyEntity memberReply,
      SurveyResponseRequestDto responseRequestDto) {
    memberReply.getSurveyReplyExcuseEntity()
        .modifyExcuse(responseRequestDto.getExcuse());
  }

  public SurveyInformationResponseDto getSurveyInformation(Long surveyId, Long memberId) {
    SurveyEntity survey = surveyRepository.findById(surveyId)
        .orElseThrow(CustomSurveyNotFoundException::new);
    MemberEntity member = memberUtilService.getById(memberId);

    Boolean isResponded = false;
    SurveyMemberReplyEntity surveyMemberReplyEntity = null;
    if (isUserRespondedSurvey(survey, member)) {
      isResponded = true;
      surveyMemberReplyEntity = surveyMemberReplyRepository.findBySurveyIdAndMemberId(
              surveyId, memberId)
          .orElseThrow(CustomSurveyMemberReplyNotFoundException::new);
    }

    return SurveyInformationResponseDto.from(survey, surveyMemberReplyEntity, isResponded);
  }

  private Boolean isUserRespondedSurvey(SurveyEntity survey, MemberEntity member) {
    return getSurveyRespondedMemberList(survey)
        .contains(member);
  }

  private List<MemberEntity> getSurveyRespondedMemberList(SurveyEntity survey) {
    return survey.getRespondents()
        .stream()
        .map(SurveyMemberReplyEntity::getMember)
        .toList();
  }

  public Long getLatestVisibleSurveyId() {
    LocalDateTime now = LocalDateTime.now();
    SurveyEntity survey = surveyRepository.findTop1ByOpenTimeBeforeAndCloseTimeAfterAndIsVisibleTrueOrderByCloseTimeDesc(
            now, now)
        .orElse(NO_SURVEY);

    return survey.getId();
  }

  public ClosedSurveyInformationResponseDto getLatestClosedSurveyInformation() {
    LocalDateTime now = LocalDateTime.now();
    SurveyEntity latestClosedSurvey = surveyRepository
        .findTop1ByCloseTimeBeforeAndIsVisibleTrueOrderByCloseTimeDesc(now)
        .orElse(NO_SURVEY);
    Long reqMemberId = authService.getMemberIdByJWT();

    Optional<SurveyMemberReplyEntity> surveyMemberReply = surveyMemberReplyRepository
        .findBySurveyIdAndMemberId(latestClosedSurvey.getId(), reqMemberId);
    if (surveyMemberReply.isEmpty()) {
      return ClosedSurveyInformationResponseDto.notFound();
    }
    return ClosedSurveyInformationResponseDto.from(latestClosedSurvey, surveyMemberReply.get());
  }

}
