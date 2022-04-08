package keeper.project.homepage.user.dto.library;

import java.time.LocalDateTime;
import java.util.Date;
import keeper.project.homepage.common.controller.util.ImageController;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.util.EnvironmentProperty;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
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
public class BookResult {

  private Long id;
  private String title;
  private String author;
  private String information;
  private String department;
  private Long total;
  private Long borrow;
  private Long enable;
  private Date registerDate;
  private String thumbnailPath;

  public void initWithEntity(BookEntity bookEntity) {
    this.id = bookEntity.getId();
    this.title = bookEntity.getTitle();
    this.author = bookEntity.getAuthor();
    this.information = bookEntity.getInformation();
    if (bookEntity.getDepartment() != null) {
      this.department = bookEntity.getDepartment().getName();
    }
    this.total = bookEntity.getTotal();
    this.borrow = bookEntity.getBorrow();
    this.enable = bookEntity.getEnable();
    this.registerDate = bookEntity.getRegisterDate();
    // TODO : book default image가 없어서 임시로 posting default image를 넣었음.
    //   나중에 default image가 생기면 -> DefaultThumbnailInfo.ThumbBook의 id를 수정할 것.
    this.thumbnailPath = bookEntity.getThumbnailId() == null ?
        EnvironmentProperty.getThumbnailPath(DefaultThumbnailInfo.ThumbBook.getThumbnailId())
        : EnvironmentProperty.getThumbnailPath(bookEntity.getThumbnailId().getId());

  }
}
