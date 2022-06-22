package keeper.project.homepage.user.dto.attendance;

import java.util.List;
import keeper.project.homepage.entity.member.MemberEntity;
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
public class RankDto {

  private Long id;
  private String nickName;
  private String thumbnailPath;
  private List<String> jobs;
  private Integer point;
  private Long rank;

  public static RankDto toDto(MemberEntity member) {
    return RankDto.builder()
        .id(member.getId())
        .nickName(member.getNickName())
        .point(member.getPoint())
        .thumbnailPath(member.getThumbnailPath())
        .jobs(member.getJobs())
        .build();
  }
}
