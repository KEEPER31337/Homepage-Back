package keeper.project.homepage.dto.library;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {

  private String title;
  private String author;
  private String information;
  private Integer department;
  private Long quantity;
  private Long thumbnailId;

}
