package keeper.project.homepage.dto.rank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.Date;
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
public class RankResult {

  private Long id;
  private String nickName;
  private Long thumbnailId;
  private List<String> jobs;
  private Integer point;
  private Integer rank;

  public void initWithEntity(MemberEntity memberEntity) {
    // 민감한 정보 제외
    this.id = memberEntity.getId();
    this.nickName = memberEntity.getNickName();
    this.point = memberEntity.getPoint();
    if (memberEntity.getThumbnail() != null) {
      this.thumbnailId = memberEntity.getThumbnail().getId();
    }
    if (memberEntity.getMemberJobs() != null || memberEntity.getMemberJobs().isEmpty() == false) {
      this.jobs = new ArrayList<>();
      memberEntity.getMemberJobs()
          .forEach(job -> this.jobs.add(job.getMemberJobEntity().getName()));
    }
  }
}
