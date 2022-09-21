package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.member.dto.CommonMemberDto;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(Include.NON_NULL)
public class CtfContestDto {

  protected String name;
  protected String description;

  @JsonProperty(access = Access.READ_ONLY)
  protected Long ctfId;
  @JsonProperty(access = Access.READ_ONLY)
  protected CommonMemberDto creator;

  public static CtfContestDto toDto(CtfContestEntity ctfContestEntity) {
    return CtfContestDto.builder()
        .ctfId(ctfContestEntity.getId())
        .name(ctfContestEntity.getName())
        .description(ctfContestEntity.getDescription())
        .creator(CommonMemberDto.toDto(ctfContestEntity.getCreator()))
        .build();
  }
}
