package keeper.project.homepage.admin.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.member.MemberEntity;
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
public class CtfContestDto {

  @JsonProperty(access = Access.READ_ONLY)
  private Long ctfId;

  private String name;

  private String description;

  @JsonProperty(access = Access.READ_ONLY)
  private Boolean joinable;

  @JsonProperty(access = Access.READ_ONLY)
  private CommonMemberDto creator;

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
