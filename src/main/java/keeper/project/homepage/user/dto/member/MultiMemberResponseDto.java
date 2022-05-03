package keeper.project.homepage.user.dto.member;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultiMemberResponseDto {

  private Long id;
  private String nickname;
  private String thumbnailPath;
  private Float generation;
  private List<String> memberJobs;
  private String memberType;
  private String msg;

}
