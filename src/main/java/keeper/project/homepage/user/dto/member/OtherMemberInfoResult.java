package keeper.project.homepage.user.dto.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.common.controller.util.ImageController;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.util.EnvironmentProperty;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OtherMemberInfoResult {

  private Long memberId;

  private String nickName;

  private Date birthday;

  private String memberType;

  private String memberRank;

  private List<String> memberJobs;

  private String thumbnailPath;

  private Float generation;

  private Boolean checkFollowee;

  private Boolean checkFollower;

  public OtherMemberInfoResult(MemberEntity memberEntity) {
    this.memberId = memberEntity.getId();
    this.nickName = memberEntity.getNickName();
    this.birthday = memberEntity.getBirthday();
    if (memberEntity.getMemberType() != null) {
      this.memberType = memberEntity.getMemberType().getName();
    }
    if (memberEntity.getMemberRank() != null) {
      this.memberRank = memberEntity.getMemberRank().getName();
    }
    if (memberEntity.getMemberJobs() != null || !memberEntity.getMemberJobs().isEmpty()) {
      this.memberJobs = new ArrayList<>();
      memberEntity.getMemberJobs()
          .forEach(job ->
              this.memberJobs.add(job.getMemberJobEntity().getName()));
    }
    this.thumbnailPath = memberEntity.getThumbnailPath();
    this.generation = memberEntity.getGeneration();
  }

  public void setCheckFollow(Boolean checkFollowee, Boolean checkFollower) {
    this.checkFollowee = checkFollowee;
    this.checkFollower = checkFollower;
  }
}