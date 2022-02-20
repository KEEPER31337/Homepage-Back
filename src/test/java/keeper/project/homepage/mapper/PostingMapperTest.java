package keeper.project.homepage.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.time.LocalDateTime;
import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.repository.FileRepository;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.repository.posting.CategoryRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;

@Log4j2
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest
public class PostingMapperTest {

  private final PostingMapper postingMapper = Mappers.getMapper(PostingMapper.class);

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ThumbnailRepository thumbnailRepository;

  @Autowired
  private FileRepository fileRepository;

  private final String generalTestImage = "keeper_files" + File.separator + "image.jpg";
  private final String generalThumbnailImage =
      "keeper_files" + File.separator + "thumbnail" + File.separator + "t_image.jpg";
  private ThumbnailEntity generalThumbnail;
  private FileEntity generalImageFile;
  final private String ipAddress1 = "127.0.0.1";

  private String getFileName(String filePath) {
    File file = new File(filePath);
    return file.getName();
  }

  @BeforeEach
  public void setup() {
    generalImageFile = FileEntity.builder()
        .fileName(getFileName(generalTestImage))
        .filePath(generalTestImage)
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(generalImageFile);

    generalThumbnail = ThumbnailEntity.builder()
        .path(generalThumbnailImage)
        .file(generalImageFile).build();
    thumbnailRepository.save(generalThumbnail);
  }

  @Test
  public void toEntityTest() throws Exception {
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    //프론트에서 넘겨주는 정보
    PostingDto postingDto = PostingDto.builder()
        .title("test 게시판 제목")
        .content("test 게시판 제목 내용")
        .categoryId(1L)
        .ipAddress("192.11.222.333")
        .allowComment(0)
        .isNotice(0)
        .isTemp(0)
        .isSecret(1)
        .password("")
        .build();

    //백 서버내에서 처리하는 정보
    postingDto.setRegisterTime(LocalDateTime.now());
    postingDto.setUpdateTime(LocalDateTime.now());
    postingDto.setThumbnailId(generalThumbnail.getId());

    PostingEntity postingEntity = postingMapper.toEntity(postingDto, categoryRepository,
        thumbnailRepository);

    log.info(mapper.writeValueAsString(postingEntity));
  }
}
