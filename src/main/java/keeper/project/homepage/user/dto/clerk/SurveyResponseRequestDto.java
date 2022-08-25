package keeper.project.homepage.user.dto.clerk;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
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
public class SurveyResponseRequestDto {
  private Long memberId;
  @NotNull(message = "응답 ID는 필수 입력입니다.")
  private Long replyId;
  @Nullable
  private String excuse;
  private LocalDateTime replyTime;
}
