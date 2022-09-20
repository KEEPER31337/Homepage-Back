package keeper.project.homepage.clerk.dto.response;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeminarResponseDto {

  @NonNull
  private Long id;

  @NonNull
  private String name;

  @NonNull
  private LocalDateTime openTime;

  public static SeminarResponseDto from(SeminarEntity seminarEntity) {
    return SeminarResponseDto.builder()
        .id(seminarEntity.getId())
        .name(seminarEntity.getName())
        .openTime(seminarEntity.getOpenTime())
        .build();
  }
}
