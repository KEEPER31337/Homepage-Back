package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
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
public class CtfChallengeTypeDto {

  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @JsonProperty(access = Access.READ_ONLY)
  private String name;

  public static CtfChallengeTypeDto toDto(CtfChallengeTypeEntity ctfChallengeTypeEntity) {
    return CtfChallengeTypeDto.builder()
        .id(ctfChallengeTypeEntity.getId())
        .name(ctfChallengeTypeEntity.getName())
        .build();

  }
}
