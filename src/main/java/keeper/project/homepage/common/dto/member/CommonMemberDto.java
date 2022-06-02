package keeper.project.homepage.common.dto.member;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonMemberDto {

  private Long id;
  private String nickName;
  private List<String> jobs;
  private String thumbnailPath;
  private Float generation;

  public static CommonMemberDto toDto(MemberEntity memberEntity) {
    return CommonMemberDto.builder()
        .id(memberEntity.getId())
        .nickName(memberEntity.getNickName())
        .jobs(memberEntity.getJobs())
        .thumbnailPath(memberEntity.getThumbnailPath())
        .generation(memberEntity.getGeneration())
        .build();
  }
}
