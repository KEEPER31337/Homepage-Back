package keeper.project.homepage.common.dto.member;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonMemberDto {

  private Long id;
  private String nickName;
  private List<String> jobs;
  private Long thumbnailId;
  private Float generation;
}
