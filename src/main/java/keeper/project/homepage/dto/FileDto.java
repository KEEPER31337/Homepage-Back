package keeper.project.homepage.dto;

import java.time.ZoneOffset;
import java.util.Date;
import java.time.LocalDateTime;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

  private String fileName;
  private String filePath;
  private Long fileSize;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime uploadTime;
  private String ipAddress;

  public FileEntity toEntity(PostingEntity postingEntity) {

    FileEntity fileEntity = FileEntity.builder().fileName(fileName).filePath(filePath)
        .fileSize(fileSize).uploadTime(Date.from(uploadTime.toInstant(ZoneOffset.UTC)))
        .ipAddress(ipAddress).postingId(postingEntity)
        .build();

    return fileEntity;
  }
}
