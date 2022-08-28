package keeper.project.homepage.util.dto;

import keeper.project.homepage.entity.ThumbnailEntity;
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
public class ThumbnailFileIdDto {

  private Long thumbnailId;
  private Long fileId;

  public static ThumbnailFileIdDto from(ThumbnailEntity entity) {
    return ThumbnailFileIdDto.builder()
        .thumbnailId(entity.getId())
        .fileId(entity.getFile().getId())
        .build();
  }

}
