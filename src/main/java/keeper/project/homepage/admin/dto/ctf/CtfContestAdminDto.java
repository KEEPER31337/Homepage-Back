package keeper.project.homepage.admin.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.ctf.CtfContestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class CtfContestAdminDto extends CtfContestDto {

  @JsonProperty(access = Access.READ_ONLY)
  private Boolean joinable;

  public static CtfContestAdminDto toDto(CtfContestEntity ctfContestEntity) {
    return CtfContestAdminDto.builder()
        .ctfId(ctfContestEntity.getId())
        .name(ctfContestEntity.getName())
        .description(ctfContestEntity.getDescription())
        .joinable(ctfContestEntity.getIsJoinable())
        .creatorId(ctfContestEntity.getCreator().getId())
        .build();
  }

  public CtfContestEntity toEntity(MemberEntity creator) {
    return CtfContestEntity.builder()
        .name(name)
        .description(description)
        .registerTime(LocalDateTime.now())
        .isJoinable(joinable)
        .creator(creator)
        .build();
  }
}
