package keeper.project.homepage.member.dto.response;

import java.util.List;
import keeper.project.homepage.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiMemberResponseDto {

  private Long id;
  private String nickName;
  private String thumbnailPath;
  private Float generation;
  private List<String> jobs;
  private String type;
  private String msg;

  public static MultiMemberResponseDto from(MemberEntity member) {
    return MultiMemberResponseDto.builder()
        .id(member.getId())
        .nickName(member.getNickName())
        .thumbnailPath(member.getThumbnailPath())
        .generation(member.getGeneration())
        .jobs(member.getJobs())
        .type(member.getMemberType().getName())
        .msg("Success")
        .build();
  }
}
