package keeper.project.homepage.ctf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.ctf.CtfChallengeCategoryDto;
import keeper.project.homepage.user.dto.ctf.CtfChallengeDto;
import keeper.project.homepage.user.dto.ctf.CtfChallengeTypeDto;
import keeper.project.homepage.user.dto.ctf.CtfDynamicChallengeInfoDto;
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
public class CtfChallengeAdminDto extends CtfChallengeDto {

  private Boolean isSolvable;
  private String flag;
  protected CtfChallengeTypeDto type;

  @JsonProperty(access = Access.READ_ONLY)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime registerTime;

  private CtfDynamicChallengeInfoDto dynamicInfo;

  public CtfChallengeEntity toEntity(CtfContestEntity contest, CtfChallengeTypeEntity type,
      CtfChallengeCategoryEntity category, FileEntity fileEntity, MemberEntity creator) {
    return CtfChallengeEntity.builder()
        .name(title)
        .description(content)
        .ctfContestEntity(contest)
        .ctfChallengeCategoryEntity(category)
        .ctfChallengeTypeEntity(type)
        .isSolvable(isSolvable)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .score(score)
        .fileEntity(fileEntity)
        .ctfFlagEntity(new ArrayList<>())
        .build();
  }

  public static CtfChallengeAdminDto toDto(CtfChallengeEntity challenge) {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        challenge.getCtfChallengeCategoryEntity());
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
        .category(category)
        .type(type)
        .flag(challenge.getCtfFlagEntity().get(0).getContent())
        .isSolvable(challenge.getIsSolvable())
        .registerTime(challenge.getRegisterTime())
        .creatorName(challenge.getCreator().getNickName())
        .score(challenge.getScore())
        .file(file)
        .dynamicInfo(dynamicInfo)
        .build();
  }
}
