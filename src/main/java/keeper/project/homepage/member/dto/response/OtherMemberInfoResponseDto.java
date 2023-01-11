package keeper.project.homepage.member.dto.response;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OtherMemberInfoResponseDto {

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

  public static OtherMemberInfoResponseDto from(MemberEntity member) {
    return OtherMemberInfoResponseDto.builder()
        .memberId(member.getId())
        .nickName(member.getNickName())
        .birthday(member.getBirthday())
        .memberType(
            member.getMemberType() != null ? member.getMemberType().getName() : null
        )
        .memberRank(
            member.getMemberRank() != null ? member.getMemberRank().getName() : null
        )
        .memberJobs(
            member.getMemberJobs() != null ?
                member.getMemberJobs()
                    .stream()
                    .map(job -> job.getMemberJobEntity().getName())
                    .collect(Collectors.toList()) : Collections.emptyList()
        )
        .thumbnailPath(member.getThumbnailPath())
        .generation(member.getGeneration())
        .build();
  }

  public void setCheckFollow(Boolean checkFollowee, Boolean checkFollower) {
    this.checkFollowee = checkFollowee;
    this.checkFollower = checkFollower;
  }
}