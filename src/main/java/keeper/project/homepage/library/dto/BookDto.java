package keeper.project.homepage.library.dto;

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
  private Long department;
  private Long quantity;
  private Long thumbnailId;

}
