package keeper.project.homepage.admin.dto.election.request;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import keeper.project.homepage.entity.election.ElectionEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectionRequestDto {

  @NotBlank(message = "투표 입력이 필수적으로 필요합니다.")
  @Size(max = 45, message = "투표 이름은 45글자 이하로 작성이 필요합니다.")
  private String name;
  @Size(max = 200, message = "투표 설명은 200자 이하로 작성이 필요합니다.")
  private String description;
  private LocalDateTime registerTime;
  @NotNull(message = "투표 진행 여부 설정이 필요합니다.")
  private Boolean isAvailable;

  public ElectionEntity toEntity(MemberEntity creator) {
    return ElectionEntity.builder()
        .name(this.name)
        .description(this.description)
        .registerTime(this.registerTime)
        .creator(creator)
        .isAvailable(this.isAvailable)
        .build();
  }

}
