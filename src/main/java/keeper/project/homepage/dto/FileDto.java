package keeper.project.homepage.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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
}
