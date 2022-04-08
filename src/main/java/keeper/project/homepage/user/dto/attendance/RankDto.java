package keeper.project.homepage.user.dto.attendance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.common.controller.util.ImageController;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.util.EnvironmentProperty;
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
  private Integer rank;

  public void initWithEntity(MemberEntity memberEntity) {
    // 민감한 정보 제외
    this.id = memberEntity.getId();
    this.nickName = memberEntity.getNickName();
    this.point = memberEntity.getPoint();
    this.thumbnailPath = memberEntity.getThumbnailPath();
    if (memberEntity.getMemberJobs() != null || memberEntity.getMemberJobs().isEmpty() == false) {
      this.jobs = new ArrayList<>();
      memberEntity.getMemberJobs()
          .forEach(job -> this.jobs.add(job.getMemberJobEntity().getName()));
    }
  }
}
