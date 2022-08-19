package keeper.project.homepage.admin.dto.clerk.response;

import java.util.List;
import keeper.project.homepage.admin.dto.member.job.JobDto;
import keeper.project.homepage.admin.dto.member.type.TypeDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClerkMemberJobTypeResponseDto {

  @NonNull
  private Long memberId;
  @NonNull
  private Float generation;
  @NonNull
  private List<JobDto> hasJobs;
  @NonNull
  private TypeDto type;

  public static ClerkMemberJobTypeResponseDto toDto(MemberEntity member) {
    return ClerkMemberJobTypeResponseDto.builder()
        .memberId(member.getId())
        .generation(member.getGeneration())
        .hasJobs(getJobsMemberHas(member))
        .type(TypeDto.toDto(member.getMemberType()))
        .build();
  }

  private static List<JobDto> getJobsMemberHas(MemberEntity member) {
    return member.getMemberJobs().stream()
        .map(MemberHasMemberJobEntity::getMemberJobEntity)
        .map(JobDto::toDto)
        .toList();
  }
}
