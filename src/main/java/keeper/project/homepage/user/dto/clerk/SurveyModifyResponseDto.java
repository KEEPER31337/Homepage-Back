package keeper.project.homepage.user.dto.clerk;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyEntity;
import keeper.project.homepage.entity.clerk.SurveyReplyExcuseEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyModifyResponseDto {

  private Long surveyId;
  private Long memberId;
  private Long replyId;
  @Nullable
  private String excuse;
  private LocalDateTime replyTime;

  public static SurveyModifyResponseDto toDto(SurveyMemberReplyEntity surveyMemberReplyEntity,
      String excuse) {
    return SurveyModifyResponseDto.builder()
        .surveyId(surveyMemberReplyEntity.getSurvey().getId())
        .memberId(surveyMemberReplyEntity.getMember().getId())
        .replyId(surveyMemberReplyEntity.getReply().getId())
        .excuse(excuse)
        .replyTime(surveyMemberReplyEntity.getReplyTime())
        .build();
  }

  public static SurveyMemberReplyEntity toEntity(SurveyEntity survey, MemberEntity member,
      SurveyReplyEntity reply, LocalDateTime replyTime, SurveyReplyExcuseEntity excuse) {
    return SurveyMemberReplyEntity.builder()
        .survey(survey)
        .member(member)
        .reply(reply)
        .replyTime(replyTime)
        .surveyReplyExcuseEntity(excuse)
        .build();
  }
}
