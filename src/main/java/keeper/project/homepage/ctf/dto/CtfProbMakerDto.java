package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class CtfProbMakerDto {

  private Long memberId;

  @JsonProperty(access = Access.READ_ONLY)
  private String nickName;

  @JsonProperty(access = Access.READ_ONLY)
  private String thumbnailPath;

  @JsonProperty(access = Access.READ_ONLY)
  private Float generation;

  public static CtfProbMakerDto toDto(MemberEntity member) {
    return CtfProbMakerDto.builder()
        .memberId(member.getId())
        .nickName(member.getNickName())
        .generation(member.getGeneration())
        .thumbnailPath(member.getThumbnailPath())
        .build();
  }
}
