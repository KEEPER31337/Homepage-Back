package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.ctf.entity.CtfChallengeCategoryEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeHasCtfChallengeCategoryEntity;
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

  private Long id;

  @JsonProperty(access = Access.READ_ONLY)
  private String name;

  public static CtfChallengeCategoryEntity toEntity(CtfChallengeCategoryDto ctfChallengeCategoryDto){
    return CtfChallengeCategoryEntity.builder()
        .id(ctfChallengeCategoryDto.getId())
        .name(ctfChallengeCategoryDto.getName())
        .build();
  }

  public static CtfChallengeCategoryDto toDto(CtfChallengeCategoryEntity ctfChallengeCategoryEntity) {
    return CtfChallengeCategoryDto.builder()
        .id(ctfChallengeCategoryEntity.getId())
        .name(ctfChallengeCategoryEntity.getName())
        .build();
  }

  public static CtfChallengeCategoryDto toDto(CtfChallengeHasCtfChallengeCategoryEntity ctfChallengeHasCtfChallengeCategoryEntity) {
    return CtfChallengeCategoryDto.builder()
        .id(ctfChallengeHasCtfChallengeCategoryEntity.getCategory().getId())
        .name(ctfChallengeHasCtfChallengeCategoryEntity.getCategory().getName())
        .build();
  }
}
