package keeper.project.homepage.user.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
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
public class CtfChallengeDto extends CtfCommonChallengeDto {

  protected String content;

  @JsonProperty(access = Access.READ_ONLY)
  protected String creatorName;
  @JsonProperty(access = Access.READ_ONLY)
  protected Long solvedTeamCount;
  @JsonProperty(access = Access.READ_ONLY)
  @JsonInclude
  protected FileDto file;

  public static CtfChallengeDto toDto(CtfChallengeEntity challenge, Long solvedTeamCount) {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        challenge.getCtfChallengeCategoryEntity());
    FileDto file = FileDto.toDto(challenge.getFileEntity());

    return CtfChallengeDto.builder()
        .challengeId(challenge.getId())
        .title(challenge.getName())
        .content(challenge.getDescription())
        .contestId(challenge.getCtfContestEntity().getId())
        .category(category)
        .creatorName(challenge.getCreator().getNickName())
        .score(challenge.getScore())
        .solvedTeamCount(solvedTeamCount)
        .file(file)
        .build();
  }
}
