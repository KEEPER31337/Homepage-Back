package keeper.project.homepage.user.service.clerk;

import static keeper.project.homepage.util.service.SurveyUtilService.Reply.OTHER_DORMANT;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyExcuseEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.clerk.CustomSurveyInVisibleException;
import keeper.project.homepage.exception.clerk.CustomSurveyNotFoundException;
import keeper.project.homepage.repository.clerk.SurveyMemberReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyExcuseRepository;
import keeper.project.homepage.repository.clerk.SurveyReplyRepository;
import keeper.project.homepage.repository.clerk.SurveyRepository;
import keeper.project.homepage.user.dto.clerk.SurveyInformationRequestDto;
import keeper.project.homepage.user.dto.clerk.SurveyModifyResponseDto;
import keeper.project.homepage.user.dto.clerk.SurveyResponseRequestDto;
import keeper.project.homepage.user.dto.clerk.SurveyInformationDto;
import keeper.project.homepage.user.service.member.MemberUtilService;
import keeper.project.homepage.util.service.SurveyUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    if (reply.getId() == OTHER_DORMANT.getReplyId()) {
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
    SurveyMemberReplyEntity memberReply = surveyMemberReplyRepository.findBySurveyId(surveyId);
    SurveyReplyEntity reply = surveyReplyRepository.getById(requestDto.getReplyId());

    modifyReplyAndExcuse(memberReply, reply, requestDto);

    return SurveyModifyResponseDto.toDto(surveyMemberReplyRepository.save(memberReply),
        requestDto.getExcuse());
  }

  private void modifyReplyAndExcuse(SurveyMemberReplyEntity memberReply, SurveyReplyEntity reply,
      SurveyResponseRequestDto requestDto) {
    memberReply.setReply(reply);
    memberReply.setReplyTime(LocalDateTime.now());

    SurveyReplyExcuseEntity beforeExcuse = memberReply.getSurveyReplyExcuseEntity();

    if (isBeforeExcuseNull(beforeExcuse)) {
      if (isReplyOtherDormant(requestDto)) {
        surveyUtilService.generateSurveyReplyExcuse(memberReply,
            requestDto.getExcuse());
      }
    } else {
      if (isReplyOtherDormant(requestDto)) {
        memberReply.getSurveyReplyExcuseEntity()
            .setRestExcuse(requestDto.getExcuse());
      } else {
        deleteBeforeExcuseAndSetExcuse(beforeExcuse, memberReply, requestDto);
      }
    }
  }

  private Boolean isBeforeExcuseNull(SurveyReplyExcuseEntity excuse) {
    if (excuse == null) {
      return true;
    } else {
      return false;
    }
  }

  private Boolean isReplyOtherDormant(SurveyResponseRequestDto requestDto) {
    if (requestDto.getReplyId() == OTHER_DORMANT.getReplyId()) {
      return true;
    } else {
      return false;
    }
  }

  private void deleteBeforeExcuseAndSetExcuse(SurveyReplyExcuseEntity beforeExcuse,
      SurveyMemberReplyEntity memberReply,
      SurveyResponseRequestDto responseRequestDto) {
    surveyReplyExcuseRepository.delete(beforeExcuse);
    memberReply.getSurveyReplyExcuseEntity()
        .setRestExcuse(responseRequestDto.getExcuse());
  }

  public SurveyInformationDto getSurveyInformation(SurveyInformationRequestDto requestDto) {
    SurveyEntity survey = surveyRepository.findById(requestDto.getSurveyId()).orElseThrow(
        CustomSurveyNotFoundException::new);
    MemberEntity member = memberUtilService.getById(requestDto.getMemberId());

    Boolean isResponded = false;
    String reply = null;

    if (isUserRespondedSurvey(survey, member)) {
      isResponded = true;
      SurveyMemberReplyEntity surveyMemberReplyEntity = surveyMemberReplyRepository.findByMemberId(
          requestDto.getMemberId());
      reply = surveyMemberReplyEntity.getReply().getType();
    } else {
      isResponded = false;
    }

    return SurveyInformationDto.toDto(survey, reply, isResponded);
  }

  private Boolean isUserRespondedSurvey(SurveyEntity survey, MemberEntity member) {
    if (survey.getRespondents().stream().map(SurveyMemberReplyEntity::getMember).toList()
        .contains(member)) {
      return true;
    } else {
      return false;
    }
  }

}
