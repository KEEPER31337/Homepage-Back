package keeper.project.homepage.admin.dto.ctf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeCategoryEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeEntity;
import keeper.project.homepage.entity.ctf.CtfChallengeTypeEntity;
import keeper.project.homepage.entity.ctf.CtfContestEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.user.dto.ctf.CtfChallengeCategoryDto;
import keeper.project.homepage.user.dto.ctf.CtfChallengeTypeDto;
import keeper.project.homepage.util.dto.FileDto;
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
public class CtfChallengeAdminDto {

  @JsonProperty(access = Access.READ_ONLY)
  private Long challengeId;

  private String title;
  private String content;
  private CtfChallengeCategoryDto category;
  private CtfChallengeTypeDto type;
  private String flag;
  private Long score;
  private CtfContestDto contest;
  @JsonProperty(access = Access.READ_ONLY)
  private FileDto file;

  public CtfChallengeEntity toEntity(CtfContestEntity contest, CtfChallengeTypeEntity type,
      CtfChallengeCategoryEntity category, FileEntity fileEntity, MemberEntity creator) {
    return CtfChallengeEntity.builder()
        .name(title)
        .description(content)
        .ctfContestEntity(contest)
        .ctfChallengeCategoryEntity(category)
        .ctfChallengeTypeEntity(type)
        .isSolvable(false)
        .registerTime(LocalDateTime.now())
        .creator(creator)
        .score(score)
        .fileEntity(fileEntity)
        .build();
  }

  public static CtfChallengeAdminDto toDto(CtfChallengeEntity challenge) {
    CtfChallengeCategoryDto category = CtfChallengeCategoryDto.toDto(
        challenge.getCtfChallengeCategoryEntity());
    CtfChallengeTypeDto type = CtfChallengeTypeDto.toDto(
        challenge.getCtfChallengeTypeEntity());
    CtfContestDto contest = CtfContestDto.toDto(
        challenge.getCtfContestEntity());
    FileDto file = FileDto.toDto(challenge.getFileEntity());

    return CtfChallengeAdminDto.builder()
        .challengeId(challenge.getId())
        .title(challenge.getName())
        .content(challenge.getDescription())
        .category(category)
        .type(type)
        .contest(contest)
        .score(challenge.getScore())
        .flag(challenge.getCtfFlagEntity().get(0).getContent())
        .file(file)
        .build();
  }
}
