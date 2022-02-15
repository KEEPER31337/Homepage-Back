package keeper.project.homepage.dto.attendance;

import keeper.project.homepage.entity.ThumbnailEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceForListDto {

  private String ipAddress;
  private String nickName;
  private ThumbnailEntity thumbnail;
  private String greetings;
  private Integer continousDay;
  private Integer rank;

}
