package keeper.project.homepage.admin.dto.member.response;

import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberByRealNameResponseDto {

  private Long memberId;
  private String realName;
  private Float generation;
  private String thumbnailPath;

  public static MemberByRealNameResponseDto from(MemberEntity member) {
    return MemberByRealNameResponseDto.builder()
        .memberId(member.getId())
        .generation(member.getGeneration())
        .realName(member.getRealName())
        .thumbnailPath(member.getThumbnailPath())
        .build();
  }
}
