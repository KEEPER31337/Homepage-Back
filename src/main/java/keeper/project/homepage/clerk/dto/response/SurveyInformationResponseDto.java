package keeper.project.homepage.clerk.dto.response;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import keeper.project.homepage.clerk.entity.SurveyEntity;
import keeper.project.homepage.clerk.entity.SurveyMemberReplyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyInformationResponseDto {

  @NonNull
  private Long surveyId;
  @NonNull
  private String surveyName;
  @NonNull
  private LocalDateTime openTime;
  @NonNull
  private LocalDateTime closeTime;
  @NotNull
  private String description;
  @NonNull
  private Boolean isVisible;
  @NonNull
  private Boolean isResponded;
  @Nullable
  private Long replyId;
  @Nullable
  private String excuse;

  public static SurveyInformationResponseDto of(SurveyEntity survey,
      SurveyMemberReplyEntity surveyMemberReplyEntity,
      Boolean isResponded) {
    return SurveyInformationResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .openTime(survey.getOpenTime())
        .closeTime(survey.getCloseTime())
        .description(survey.getDescription())
        .isVisible(survey.getIsVisible())
        .isResponded(isResponded)
        .replyId(checkReplyNull(surveyMemberReplyEntity))
        .excuse(checkReplyHasExcuse(surveyMemberReplyEntity))
        .build();
  }

  private static Long checkReplyNull(SurveyMemberReplyEntity surveyMemberReplyEntity) {
    Long replyId = null;
    if (surveyMemberReplyEntity != null) {
      replyId = surveyMemberReplyEntity.getReply().getId();
    }
    return replyId;
  }

  private static String checkReplyHasExcuse(SurveyMemberReplyEntity surveyMemberReplyEntity) {
    String excuse = null;
    if ((surveyMemberReplyEntity != null)
        && surveyMemberReplyEntity.getSurveyReplyExcuseEntity() != null) {
      excuse = surveyMemberReplyEntity.getSurveyReplyExcuseEntity().getRestExcuse();
    }
    return excuse;
  }
}
