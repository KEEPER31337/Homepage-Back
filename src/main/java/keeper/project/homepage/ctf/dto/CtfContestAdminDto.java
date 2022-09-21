package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import keeper.project.homepage.member.dto.CommonMemberDto;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.member.entity.MemberEntity;
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
public class CtfContestAdminDto extends CtfContestDto {

  @JsonProperty(access = Access.READ_ONLY)
  private Boolean joinable;

  public static CtfContestAdminDto toDto(CtfContestEntity ctfContestEntity) {
    return CtfContestAdminDto.builder()
        .ctfId(ctfContestEntity.getId())
        .name(ctfContestEntity.getName())
        .description(ctfContestEntity.getDescription())
        .joinable(ctfContestEntity.getIsJoinable())
        .creator(CommonMemberDto.toDto(ctfContestEntity.getCreator()))
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
