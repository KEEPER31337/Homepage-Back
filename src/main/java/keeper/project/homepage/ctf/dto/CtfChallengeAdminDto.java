package keeper.project.homepage.ctf.dto;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.util.dto.FileDto;
import keeper.project.homepage.util.entity.FileEntity;
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
public class CtfChallengeAdminDto extends CtfChallengeDto {

  private Boolean isSolvable;
  private String flag;
  protected CtfChallengeTypeDto type;

  @JsonProperty(access = Access.READ_ONLY)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime registerTime;

  private CtfDynamicChallengeInfoDto dynamicInfo;

  public CtfChallengeEntity toEntity(CtfContestEntity contest, CtfChallengeTypeEntity type,
      FileEntity fileEntity, MemberEntity creator) {
    return CtfChallengeEntity.builder()
        .name(title)
        .description(content)
        .ctfContestEntity(contest)
        .ctfChallengeHasCtfChallengeCategoryList(new ArrayList<>())
        .ctfChallengeTypeEntity(type)
        .isSolvable(isSolvable)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .score(score)
        .fileEntity(fileEntity)
        .ctfFlagEntity(new ArrayList<>())
        .maxSubmitCount(maxSubmitCount)
        .build();
  }

  public static CtfChallengeAdminDto toDto(CtfChallengeEntity challenge, Long solvedTeamCount) {
    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(
        challenge.getCtfChallengeTypeEntity());
    FileDto file = FileDto.toDto(
        challenge.getFileEntity());
    CtfDynamicChallengeInfoDto dynamicInfo = CtfDynamicChallengeInfoDto.toDto(
        challenge.getDynamicChallengeInfoEntity());

    return CtfChallengeAdminDto.builder()
        .challengeId(challenge.getId())
        .title(challenge.getName())
        .content(challenge.getDescription())
        .contestId(challenge.getCtfContestEntity().getId())
        .categories(challenge.getCtfChallengeHasCtfChallengeCategoryList().stream()
            .map(CtfChallengeCategoryDto::toDto)
            .collect(toList()))
        .type(type)
        .flag(getVirtualTeamFlag(challenge).getContent())
        .isSolvable(challenge.getIsSolvable())
        .registerTime(challenge.getRegisterTime())
        .creatorName(challenge.getCreator().getNickName())
        .solvedTeamCount(solvedTeamCount)
        .score(challenge.getScore())
        .file(file)
        .dynamicInfo(dynamicInfo)
        .remainedSubmitCount(getVirtualTeamFlag(challenge).getRemainedSubmitCount())
        .lastTryTime(getVirtualTeamFlag(challenge).getLastTryTime())
        .solvedTime(getVirtualTeamFlag(challenge).getSolvedTime())
        .maxSubmitCount(challenge.getMaxSubmitCount())
        .build();
  }

  static CtfFlagEntity getVirtualTeamFlag(CtfChallengeEntity challenge) {
    return challenge.getCtfFlagEntity().get(0);
  }
}
