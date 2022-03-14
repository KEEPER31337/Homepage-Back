package keeper.project.homepage.user.dto.study;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@Setter
public class StudyMemberRequestDto {

  private Long studyId;
  private Long memberId;
}
