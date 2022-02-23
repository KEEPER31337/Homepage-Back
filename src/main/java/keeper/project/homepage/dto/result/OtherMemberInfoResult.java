package keeper.project.homepage.dto.result;

import java.util.Date;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OtherMemberInfoResult {

  private String loginId;

  private String realName;

  private String nickName;

  private Date birthday;

  private Date registerDate;

  private MemberTypeEntity memberTypeEntity;

  private MemberRankEntity memberRankEntity;

  private ThumbnailEntity thumbnailEntity;

  private Float generation;

  private Boolean checkFollowee;

  private Boolean checkFollower;

  public OtherMemberInfoResult(MemberEntity memberEntity) {
    this.loginId = memberEntity.getLoginId();
    this.realName = memberEntity.getRealName();
    this.nickName = memberEntity.getNickName();
    this.birthday = memberEntity.getBirthday();
    this.registerDate = memberEntity.getRegisterDate();
    this.memberTypeEntity = memberEntity.getMemberType();
    this.memberRankEntity = memberEntity.getMemberRank();
    this.thumbnailEntity = memberEntity.getThumbnail();
    this.generation = memberEntity.getGeneration();
  }

  public void setCheckFollow(Boolean checkFollowee, Boolean checkFollower) {
    this.checkFollowee = checkFollowee;
    this.checkFollower = checkFollower;
  }
}