package keeper.project.homepage.admin.dto.clerk.response;

import keeper.project.homepage.entity.clerk.SurveyEntity;
import keeper.project.homepage.entity.clerk.SurveyMemberReplyEntity;
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
public class ClosedSurveyInformationResponseDto {

  @NonNull
  private Long surveyId;
  @Nullable
  private String surveyName;
  @Nullable
  private Long replyId;

  public static ClosedSurveyInformationResponseDto of(SurveyEntity survey,
      SurveyMemberReplyEntity surveyMemberReply) {
    return ClosedSurveyInformationResponseDto.builder()
        .surveyId(survey.getId())
        .surveyName(survey.getName())
        .replyId(surveyMemberReply.getReply().getId())
        .build();
  }

  public static ClosedSurveyInformationResponseDto notFound() {
    return ClosedSurveyInformationResponseDto.builder()
        .surveyId(-1L)
        .surveyName(null)
        .replyId(null)
        .build();
  }
}
