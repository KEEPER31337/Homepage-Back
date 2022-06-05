package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.admin.dto.ctf.CtfContestAdminDto;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
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
  protected Long creatorId;

  public static CtfContestDto toDto(CtfContestEntity ctfContestEntity) {
    return CtfContestDto.builder()
        .ctfId(ctfContestEntity.getId())
        .name(ctfContestEntity.getName())
        .description(ctfContestEntity.getDescription())
        .creatorId(ctfContestEntity.getCreator().getId())
        .build();
  }
}
