package keeper.project.homepage.dto.library;

import java.util.Date;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.entity.member.MemberEntity;
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
