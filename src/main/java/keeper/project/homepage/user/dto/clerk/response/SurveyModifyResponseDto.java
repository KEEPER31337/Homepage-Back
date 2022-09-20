package keeper.project.homepage.user.dto.clerk.response;

import java.time.LocalDateTime;
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
public class SurveyModifyResponseDto {

  @NonNull
  private Long surveyId;
  @NonNull
  private Long memberId;
  @NonNull
  private Long replyId;
  @Nullable
  private String excuse;
  @NonNull
  private LocalDateTime replyTime;

  public static SurveyModifyResponseDto from(SurveyMemberReplyEntity surveyMemberReplyEntity,
      String excuse) {
    return SurveyModifyResponseDto.builder()
        .surveyId(surveyMemberReplyEntity.getSurvey().getId())
        .memberId(surveyMemberReplyEntity.getMember().getId())
        .replyId(surveyMemberReplyEntity.getReply().getId())
        .excuse(excuse)
        .replyTime(surveyMemberReplyEntity.getReplyTime())
        .build();
  }
}
