package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
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
public class CtfChallengeCategoryDto {

  @JsonProperty(access = Access.READ_ONLY)
  private Long id;

  @JsonProperty(access = Access.READ_ONLY)
  private String name;

  public static CtfChallengeCategoryDto toDto(CtfChallengeCategoryEntity ctfChallengeCategoryEntity) {
    return CtfChallengeCategoryDto.builder()
        .id(ctfChallengeCategoryEntity.getId())
        .name(ctfChallengeCategoryEntity.getName())
        .build();
  }
}
