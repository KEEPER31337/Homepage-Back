package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import javax.validation.Valid;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
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
public class CtfChallengeDto {

  @Valid
  @JsonUnwrapped
  private CtfCommonChallengeDto commonChallengeDto;

  private String content;

  @JsonProperty(access = Access.READ_ONLY)
  private String creatorName;
  @JsonProperty(access = Access.READ_ONLY)
  private Long solvedTeamCount;
  @JsonProperty(access = Access.READ_ONLY)
  @JsonInclude
  private FileDto file;

  public static CtfChallengeDto toDto(CtfChallengeEntity challenge, Long solvedTeamCount,
      Boolean isSolved, CtfFlagEntity ctfFlagEntity) {
    FileDto file = FileDto.toDto(challenge.getFileEntity());

    return CtfChallengeDto.builder()
        .commonChallengeDto(CtfCommonChallengeDto.toDto(challenge, isSolved, ctfFlagEntity))
        .content(challenge.getDescription())
        .creatorName(challenge.getCreator().getNickName())
        .solvedTeamCount(solvedTeamCount)
        .file(file)
        .build();
  }
}
