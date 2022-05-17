package keeper.project.homepage.util.dto;

import java.time.LocalDateTime;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {

  private Long id;
  private String fileName;
  private String filePath;
  private Long fileSize;
  private LocalDateTime uploadTime;
  private String ipAddress;

  public FileEntity toEntity(PostingEntity postingEntity) {

    FileEntity fileEntity = FileEntity.builder().fileName(fileName).filePath(filePath)
        .fileSize(fileSize).uploadTime(uploadTime)
        .ipAddress(ipAddress).postingId(postingEntity)
        .build();

    return fileEntity;
  }

  public static FileDto toDto(FileEntity fileEntity) {

    return FileDto.builder()
        .id(fileEntity.getId())
        .fileName(fileEntity.getFileName())
        .filePath(fileEntity.getFilePath())
        .fileSize(fileEntity.getFileSize())
        .uploadTime(fileEntity.getUploadTime())
        .ipAddress(fileEntity.getIpAddress())
        .build();
  }
}
