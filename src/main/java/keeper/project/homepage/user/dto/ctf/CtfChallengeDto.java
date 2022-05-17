package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.util.dto.FileDto;
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
public class CtfChallengeDto {

  @JsonProperty(access = Access.READ_ONLY)
  protected Long challengeId;

  protected String title;
  protected String content;
  protected String flag;
  protected Long score;
  protected Long creatorId;
  protected CtfChallengeCategoryDto category;
  protected CtfChallengeTypeDto type;
  protected Long contestId;
  @JsonProperty(access = Access.READ_ONLY)
  protected FileDto file;
}
