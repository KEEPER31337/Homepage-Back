package keeper.project.homepage.posting.dto;

import keeper.project.homepage.util.entity.ThumbnailEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostingImageUploadResponseDto {

  private Long thumbnailId;
  private Long fileId;

  public static PostingImageUploadResponseDto from(ThumbnailEntity entity) {
    return PostingImageUploadResponseDto.builder()
        .thumbnailId(entity.getId())
        .fileId(entity.getFile().getId())
        .build();
  }

}
