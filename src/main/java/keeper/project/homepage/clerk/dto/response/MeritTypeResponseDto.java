package keeper.project.homepage.clerk.dto.response;

import keeper.project.homepage.clerk.entity.MeritTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeritTypeResponseDto {
  @NonNull
  private Long id;

  @NonNull
  private Integer merit;

  @NonNull
  private Boolean isMerit;

  @NonNull
  private String detail;

  public static MeritTypeResponseDto from(MeritTypeEntity meritType) {
    return MeritTypeResponseDto.builder()
        .id(meritType.getId())
        .merit(meritType.getMerit())
        .isMerit(meritType.getIsMerit())
        .detail(meritType.getDetail())
        .build();
  }
}
