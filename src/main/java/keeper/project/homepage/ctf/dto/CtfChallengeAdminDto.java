package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.validation.Valid;
import keeper.project.homepage.ctf.entity.CtfChallengeEntity;
import keeper.project.homepage.ctf.entity.CtfChallengeTypeEntity;
import keeper.project.homepage.ctf.entity.CtfContestEntity;
import keeper.project.homepage.ctf.entity.CtfFlagEntity;
import keeper.project.homepage.member.entity.MemberEntity;
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
public class CtfChallengeAdminDto {

  @Valid
  @JsonUnwrapped
  private CtfChallengeDto challengeDto;
  private Boolean isSolvable;
  private String flag;
  private CtfChallengeTypeDto type;

  @JsonProperty(access = Access.READ_ONLY)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime registerTime;

  private CtfDynamicChallengeInfoDto dynamicInfo;

  public CtfChallengeEntity toEntity(CtfContestEntity contest, CtfChallengeTypeEntity type,
      FileEntity fileEntity, MemberEntity creator) {
    CtfCommonChallengeDto commonChallengeDto = challengeDto.getCommonChallengeDto();
    return CtfChallengeEntity.builder()
        .name(commonChallengeDto.getTitle())
        .description(challengeDto.getContent())
        .ctfContestEntity(contest)
        .ctfChallengeHasCtfChallengeCategoryList(new ArrayList<>())
        .ctfChallengeTypeEntity(type)
        .isSolvable(isSolvable)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .score(commonChallengeDto.getScore())
        .fileEntity(fileEntity)
        .ctfFlagEntity(new ArrayList<>())
        .maxSubmitCount(commonChallengeDto.getMaxSubmitCount())
        .build();
  }

  public static CtfChallengeAdminDto toDto(CtfChallengeEntity challenge, Long solvedTeamCount) {
    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(
        challenge.getCtfChallengeTypeEntity());
    CtfDynamicChallengeInfoDto dynamicInfo = CtfDynamicChallengeInfoDto.toDto(
        challenge.getDynamicChallengeInfoEntity());

    return CtfChallengeAdminDto.builder()
        .challengeDto(
            CtfChallengeDto.toDto(challenge, solvedTeamCount, false, getVirtualTeamFlag(challenge)))
        .type(type)
        .flag(getVirtualTeamFlag(challenge).getContent())
        .isSolvable(challenge.getIsSolvable())
        .registerTime(challenge.getRegisterTime())
        .dynamicInfo(dynamicInfo)
        .build();
  }

  static CtfFlagEntity getVirtualTeamFlag(CtfChallengeEntity challenge) {
    return challenge.getCtfFlagEntity().get(0);
  }
}
