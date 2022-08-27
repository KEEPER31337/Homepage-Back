package keeper.project.homepage.admin.dto.clerk.response;

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
public class SurveyRespondentResponseDto {

  @NonNull
  private Long memberId;
  @NonNull
  private String realName;
  @NonNull
  private String thumbnailPath;
  @NonNull
  private Float generation;
  @NonNull
  private String reply;
  @Nullable
  private String excuse;

  public static SurveyRespondentResponseDto from(SurveyMemberReplyEntity entity) {
    return SurveyRespondentResponseDto.builder()
        .memberId(entity.getMember().getId())
        .realName(entity.getMember().getRealName())
        .thumbnailPath(entity.getMember().getThumbnailPath())
        .generation(entity.getMember().getGeneration())
        .reply(entity.getReply().getType())
        .excuse(checkReplyHasExcuse(entity))
        .build();
  }

  private static String checkReplyHasExcuse(SurveyMemberReplyEntity entity) {
    String excuse = "";
    if (entity.getSurveyReplyExcuseEntity() != null) {
      excuse = entity.getSurveyReplyExcuseEntity().getRestExcuse();
    }
    return excuse;
  }

}
